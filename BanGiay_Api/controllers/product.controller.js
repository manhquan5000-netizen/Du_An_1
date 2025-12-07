const Product = require("../models/Product");

/**
 * Lấy tất cả sản phẩm (có phân trang và lọc)
 * GET /api/product?page=1&limit=10&danh_muc=nam&search=...
 */
exports.getAllProducts = async (req, res) => {
  try {
    console.log("\n========== GET ALL PRODUCTS ==========");
    console.log("Query params:", JSON.stringify(req.query, null, 2));
    
    const {
      page = 1,
      limit = 100, // Tăng limit mặc định để lấy nhiều sản phẩm hơn
      danh_muc,
      thuong_hieu,
      min_price,
      max_price,
      search,
      sort_by = "createdAt",
      sort_order = "desc",
    } = req.query;

    // Xây dựng query
    const query = {};

    // Lọc theo danh mục
    if (danh_muc) {
      query.danh_muc = danh_muc;
      console.log(`Filter by category: ${danh_muc}`);
    }

    // Lọc theo thương hiệu
    if (thuong_hieu) {
      query.thuong_hieu = thuong_hieu;
      console.log(`Filter by brand: ${thuong_hieu}`);
    }

    // Lọc theo giá
    if (min_price || max_price) {
      query.gia_khuyen_mai = {};
      if (min_price) query.gia_khuyen_mai.$gte = Number(min_price);
      if (max_price) query.gia_khuyen_mai.$lte = Number(max_price);
      console.log(`Filter by price: ${min_price} - ${max_price}`);
    }

    // Tìm kiếm theo tên và mô tả
    if (search) {
      query.$or = [
        { ten_san_pham: { $regex: search, $options: "i" } },
        { mo_ta: { $regex: search, $options: "i" } },
        { thuong_hieu: { $regex: search, $options: "i" } },
      ];
      console.log(`Search: ${search}`);
    }

    // Chỉ lấy sản phẩm active
    query.trang_thai = "active";

    // Sắp xếp
    const sortOptions = {};
    sortOptions[sort_by] = sort_order === "asc" ? 1 : -1;
    console.log(`Sort by: ${sort_by} (${sort_order})`);

    // Phân trang
    const skip = (Number(page) - 1) * Number(limit);
    console.log(`Pagination: page=${page}, limit=${limit}, skip=${skip}`);

    const products = await Product.find(query)
      .sort(sortOptions)
      .skip(skip)
      .limit(Number(limit));

    const total = await Product.countDocuments(query);
    
    console.log(`Found ${products.length} products (total: ${total})`);
    if (products.length > 0) {
      console.log(`First product: ${products[0].ten_san_pham} (ID: ${products[0]._id})`);
    } else {
      const totalActive = await Product.countDocuments({ trang_thai: "active" });
      console.log(`⚠️ No products found. Total active products in DB: ${totalActive}`);
    }
    console.log("==========================================\n");

    res.json({
      success: true,
      products,
      pagination: {
        page: Number(page),
        limit: Number(limit),
        total,
        pages: Math.ceil(total / Number(limit)),
      },
    });
  } catch (err) {
    console.error("❌ Lỗi khi lấy sản phẩm:", err);
    console.error("Error stack:", err.stack);
    res.status(500).json({ 
      success: false,
      message: err.message || "Lỗi server khi lấy sản phẩm",
      error: err.message 
    });
  }
};

/**
 * Lấy sản phẩm theo ID
 * GET /api/product/:id
 */
exports.getProductById = async (req, res) => {
  try {
    const { id } = req.params;
    console.log(`\n========== GET PRODUCT BY ID ==========`);
    console.log(`Product ID: ${id}`);
    
    const product = await Product.findById(id);
    
    if (!product) {
      console.log(`❌ Product not found: ${id}`);
      return res.status(404).json({ 
        success: false,
        message: "Sản phẩm không tồn tại" 
      });
    }
    
    console.log(`✅ Product found: ${product.ten_san_pham}`);
    console.log("==========================================\n");
    
    res.json({
      success: true,
      product
    });
  } catch (err) {
    console.error("❌ Lỗi khi lấy sản phẩm theo ID:", err);
    if (err.name === "CastError") {
      return res.status(400).json({ 
        success: false,
        message: "ID sản phẩm không hợp lệ" 
      });
    }
    res.status(500).json({ 
      success: false,
      message: err.message || "Lỗi server khi lấy sản phẩm" 
    });
  }
};

/**
 * Lấy sản phẩm bán chạy
 * GET /api/product/best-selling?limit=10
 */
exports.getBestSellingProducts = async (req, res) => {
  try {
    const limit = Number(req.query.limit) || 10;
    console.log(`\n========== GET BEST SELLING PRODUCTS ==========`);
    console.log(`Limit: ${limit}`);
    
    const products = await Product.find({ trang_thai: "active" })
      .sort({ so_luong_da_ban: -1 })
      .limit(limit);
    
    console.log(`Found ${products.length} products`);
    if (products.length > 0) {
      console.log(`Top product: ${products[0].ten_san_pham} - Sold: ${products[0].so_luong_da_ban}`);
    } else {
      const totalProducts = await Product.countDocuments({ trang_thai: "active" });
      console.log(`⚠️ No products found. Total active products in DB: ${totalProducts}`);
    }
    console.log(`==========================================\n`);
    
    // Trả về array trực tiếp như Android app expect
    res.json(products);
  } catch (err) {
    console.error("❌ Error in getBestSellingProducts:", err);
    res.status(500).json({ 
      success: false,
      message: err.message || "Lỗi server khi lấy sản phẩm bán chạy" 
    });
  }
};

/**
 * Lấy sản phẩm mới nhất
 * GET /api/product/newest?limit=10
 */
exports.getNewestProducts = async (req, res) => {
  try {
    const limit = Number(req.query.limit) || 10;
    console.log(`\n========== GET NEWEST PRODUCTS ==========`);
    console.log(`Limit: ${limit}`);
    
    const products = await Product.find({ trang_thai: "active" })
      .sort({ createdAt: -1 })
      .limit(limit);
    
    console.log(`Found ${products.length} products`);
    console.log(`==========================================\n`);
    
    res.json(products);
  } catch (err) {
    console.error("❌ Error in getNewestProducts:", err);
    res.status(500).json({ 
      success: false,
      message: err.message || "Lỗi server khi lấy sản phẩm mới nhất" 
    });
  }
};

/**
 * Lấy sản phẩm theo danh mục
 * GET /api/product/category/:danh_muc
 */
exports.getProductsByCategory = async (req, res) => {
  try {
    const { danh_muc } = req.params;
    console.log(`\n========== GET PRODUCTS BY CATEGORY ==========`);
    console.log(`Category: ${danh_muc}`);
    
    // Validate danh_muc
    const validCategories = ["nam", "nu", "unisex"];
    if (!validCategories.includes(danh_muc)) {
      return res.status(400).json({
        success: false,
        message: `Danh mục không hợp lệ. Chỉ chấp nhận: ${validCategories.join(", ")}`
      });
    }
    
    const products = await Product.find({
      danh_muc,
      trang_thai: "active",
    });
    
    console.log(`Found ${products.length} products in category "${danh_muc}"`);
    if (products.length > 0) {
      console.log(`First product: ${products[0].ten_san_pham} (ID: ${products[0]._id})`);
    } else {
      const totalProducts = await Product.countDocuments({ trang_thai: "active" });
      const totalInCategory = await Product.countDocuments({ danh_muc, trang_thai: "active" });
      console.log(`⚠️ No products found in category "${danh_muc}"`);
      console.log(`Total active products in DB: ${totalProducts}`);
      console.log(`Total products in category "${danh_muc}": ${totalInCategory}`);
    }
    console.log(`==========================================\n`);
    
    // Trả về array trực tiếp như Android app expect
    res.json(products);
  } catch (err) {
    console.error("❌ Error in getProductsByCategory:", err);
    res.status(500).json({ 
      success: false,
      message: err.message || "Lỗi server khi lấy sản phẩm theo danh mục" 
    });
  }
};

/**
 * Tạo sản phẩm mới (Admin)
 * POST /api/product
 */
exports.createProduct = async (req, res) => {
  try {
    console.log("\n========== CREATE PRODUCT ==========");
    console.log("Request body:", JSON.stringify(req.body, null, 2));
    
    // Validation các trường bắt buộc
    const { ten_san_pham, gia_goc, gia_khuyen_mai, hinh_anh } = req.body;

    if (!ten_san_pham || ten_san_pham.trim() === "") {
      return res.status(400).json({ 
        success: false,
        message: "Tên sản phẩm là bắt buộc" 
      });
    }
    if (!gia_goc || gia_goc <= 0) {
      return res.status(400).json({ 
        success: false,
        message: "Giá gốc phải lớn hơn 0" 
      });
    }
    if (!gia_khuyen_mai || gia_khuyen_mai <= 0) {
      return res.status(400).json({ 
        success: false,
        message: "Giá khuyến mãi phải lớn hơn 0" 
      });
    }
    if (!hinh_anh || hinh_anh.trim() === "") {
      return res.status(400).json({ 
        success: false,
        message: "Hình ảnh là bắt buộc" 
      });
    }

    const newProduct = new Product(req.body);
    await newProduct.save();
    
    console.log(`✅ Product created: ${newProduct.ten_san_pham} (ID: ${newProduct._id})`);
    console.log("==========================================\n");
    
    res.status(201).json({ 
      success: true,
      message: "Sản phẩm được tạo thành công", 
      product: newProduct 
    });
  } catch (err) {
    console.error("❌ Lỗi khi tạo sản phẩm:", err);
    // Xử lý lỗi validation của Mongoose
    if (err.name === "ValidationError") {
      const errors = Object.values(err.errors).map((e) => e.message);
      return res.status(400).json({ 
        success: false,
        message: "Dữ liệu không hợp lệ", 
        errors: errors 
      });
    }
    res.status(500).json({ 
      success: false,
      message: err.message || "Lỗi server khi tạo sản phẩm" 
    });
  }
};

/**
 * Cập nhật sản phẩm theo ID (Admin)
 * PUT /api/product/:id
 */
exports.updateProduct = async (req, res) => {
  try {
    const { id } = req.params;
    console.log(`\n========== UPDATE PRODUCT ==========`);
    console.log(`Product ID: ${id}`);
    console.log("Update data:", JSON.stringify(req.body, null, 2));
    
    const updatedProduct = await Product.findByIdAndUpdate(
      id,
      req.body,
      {
        new: true,
        runValidators: true,
      }
    );
    
    if (!updatedProduct) {
      console.log(`❌ Product not found: ${id}`);
      return res.status(404).json({ 
        success: false,
        message: "Sản phẩm không tồn tại" 
      });
    }
    
    console.log(`✅ Product updated: ${updatedProduct.ten_san_pham}`);
    console.log("==========================================\n");
    
    res.json({
      success: true,
      message: "Cập nhật sản phẩm thành công",
      product: updatedProduct,
    });
  } catch (err) {
    console.error("❌ Lỗi khi cập nhật sản phẩm:", err);
    if (err.name === "ValidationError") {
      const errors = Object.values(err.errors).map((e) => e.message);
      return res.status(400).json({ 
        success: false,
        message: "Dữ liệu không hợp lệ",
        errors: errors 
      });
    }
    res.status(500).json({ 
      success: false,
      message: err.message || "Lỗi server khi cập nhật sản phẩm" 
    });
  }
};

/**
 * Xóa sản phẩm theo ID (Admin)
 * DELETE /api/product/:id
 */
exports.deleteProduct = async (req, res) => {
  try {
    const { id } = req.params;
    console.log(`\n========== DELETE PRODUCT ==========`);
    console.log(`Product ID: ${id}`);
    
    const deletedProduct = await Product.findByIdAndDelete(id);
    
    if (!deletedProduct) {
      console.log(`❌ Product not found: ${id}`);
      return res.status(404).json({ 
        success: false,
        message: "Sản phẩm không tồn tại" 
      });
    }
    
    console.log(`✅ Product deleted: ${deletedProduct.ten_san_pham}`);
    console.log("==========================================\n");
    
    res.json({ 
      success: true,
      message: "Xóa sản phẩm thành công" 
    });
  } catch (err) {
    console.error("❌ Lỗi khi xóa sản phẩm:", err);
    res.status(500).json({ 
      success: false,
      message: err.message || "Lỗi server khi xóa sản phẩm" 
    });
  }
};

/**
 * Cập nhật số lượng tồn kho (Admin)
 * PUT /api/product/:id/stock
 */
exports.updateStock = async (req, res) => {
  try {
    const { id } = req.params;
    const { so_luong_ton } = req.body;
    
    console.log(`\n========== UPDATE STOCK ==========`);
    console.log(`Product ID: ${id}`);
    console.log(`New stock: ${so_luong_ton}`);
    
    if (so_luong_ton === undefined || so_luong_ton < 0) {
      return res.status(400).json({
        success: false,
        message: "Số lượng tồn kho phải >= 0"
      });
    }
    
    const product = await Product.findByIdAndUpdate(
      id,
      { so_luong_ton },
      { new: true }
    );
    
    if (!product) {
      console.log(`❌ Product not found: ${id}`);
      return res.status(404).json({ 
        success: false,
        message: "Sản phẩm không tồn tại" 
      });
    }
    
    console.log(`✅ Stock updated: ${product.ten_san_pham} - New stock: ${product.so_luong_ton}`);
    console.log("==========================================\n");
    
    res.json({
      success: true,
      message: "Cập nhật số lượng tồn kho thành công",
      product,
    });
  } catch (err) {
    console.error("❌ Lỗi khi cập nhật số lượng tồn kho:", err);
    res.status(500).json({ 
      success: false,
      message: err.message || "Lỗi server khi cập nhật số lượng tồn kho" 
    });
  }
};
