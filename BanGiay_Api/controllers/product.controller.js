const Product = require("../models/Product");

// Lấy tất cả sản phẩm (có phân trang và lọc)
exports.getAllProducts = async (req, res) => {
  try {
    console.log("GET /api/product - Query:", req.query);
    const {
      page = 1,
      limit = 10,
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
    }

    // Lọc theo thương hiệu
    if (thuong_hieu) {
      query.thuong_hieu = thuong_hieu;
    }

    // Lọc theo giá
    if (min_price || max_price) {
      query.gia_khuyen_mai = {};
      if (min_price) query.gia_khuyen_mai.$gte = Number(min_price);
      if (max_price) query.gia_khuyen_mai.$lte = Number(max_price);
    }

    // Tìm kiếm theo tên và mô tả (sử dụng regex nếu chưa có text index)
    if (search) {
      query.$or = [
        { ten_san_pham: { $regex: search, $options: "i" } },
        { mo_ta: { $regex: search, $options: "i" } },
        { thuong_hieu: { $regex: search, $options: "i" } },
      ];
    }

    // Chỉ lấy sản phẩm active
    query.trang_thai = "active";

    // Sắp xếp
    const sortOptions = {};
    sortOptions[sort_by] = sort_order === "asc" ? 1 : -1;

    // Phân trang
    const skip = (Number(page) - 1) * Number(limit);

    const products = await Product.find(query)
      .sort(sortOptions)
      .skip(skip)
      .limit(Number(limit));

    const total = await Product.countDocuments(query);

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
    console.error("Lỗi khi lấy sản phẩm:", err);
    res.status(500).json({ 
      success: false,
      error: err.message || "Lỗi server khi lấy sản phẩm" 
    });
  }
};

// Lấy sản phẩm theo ID
exports.getProductById = async (req, res) => {
  try {
    const product = await Product.findById(req.params.id);
    if (!product)
      return res.status(404).json({ 
        success: false,
        message: "Sản phẩm không tồn tại" 
      });
    res.json({
      success: true,
      product
    });
  } catch (err) {
    console.error("Lỗi khi lấy sản phẩm theo ID:", err);
    if (err.name === "CastError") {
      return res.status(400).json({ 
        success: false,
        error: "ID sản phẩm không hợp lệ" 
      });
    }
    res.status(500).json({ 
      success: false,
      error: err.message || "Lỗi server khi lấy sản phẩm" 
    });
  }
};

// Tạo sản phẩm mới
exports.createProduct = async (req, res) => {
  try {
    // Validation các trường bắt buộc
    const { ten_san_pham, gia_goc, gia_khuyen_mai, hinh_anh } = req.body;

    if (!ten_san_pham) {
      return res.status(400).json({ error: "Tên sản phẩm là bắt buộc" });
    }
    if (!gia_goc || gia_goc <= 0) {
      return res.status(400).json({ error: "Giá gốc phải lớn hơn 0" });
    }
    if (!gia_khuyen_mai || gia_khuyen_mai <= 0) {
      return res.status(400).json({ error: "Giá khuyến mãi phải lớn hơn 0" });
    }
    if (!hinh_anh) {
      return res.status(400).json({ error: "Hình ảnh là bắt buộc" });
    }

    const newProduct = new Product(req.body);
    await newProduct.save();
    res
      .status(201)
      .json({ 
        success: true,
        message: "Sản phẩm được tạo thành công", 
        product: newProduct 
      });
  } catch (err) {
    console.error("Lỗi khi tạo sản phẩm:", err);
    // Xử lý lỗi validation của Mongoose
    if (err.name === "ValidationError") {
      const errors = Object.values(err.errors).map((e) => e.message);
      return res.status(400).json({ 
        success: false,
        error: "Dữ liệu không hợp lệ", 
        details: errors 
      });
    }
    res.status(500).json({ 
      success: false,
      error: err.message || "Lỗi server khi tạo sản phẩm" 
    });
  }
};

// Cập nhật sản phẩm theo ID
exports.updateProduct = async (req, res) => {
  try {
    const updatedProduct = await Product.findByIdAndUpdate(
      req.params.id,
      req.body,
      {
        new: true,
        runValidators: true,
      }
    );
    if (!updatedProduct)
      return res.status(404).json({ message: "Sản phẩm không tồn tại" });
    res.json({
      message: "Cập nhật sản phẩm thành công",
      product: updatedProduct,
    });
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

// Xóa sản phẩm theo ID
exports.deleteProduct = async (req, res) => {
  try {
    const deletedProduct = await Product.findByIdAndDelete(req.params.id);
    if (!deletedProduct)
      return res.status(404).json({ message: "Sản phẩm không tồn tại" });
    res.json({ message: "Xóa sản phẩm thành công" });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// Cập nhật số lượng tồn kho
exports.updateStock = async (req, res) => {
  try {
    const { so_luong_ton } = req.body;
    const product = await Product.findByIdAndUpdate(
      req.params.id,
      { so_luong_ton },
      { new: true }
    );
    if (!product)
      return res.status(404).json({ message: "Sản phẩm không tồn tại" });
    res.json({
      message: "Cập nhật số lượng tồn kho thành công",
      product,
    });
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

// Lấy sản phẩm theo danh mục
exports.getProductsByCategory = async (req, res) => {
  try {
    const { danh_muc } = req.params;
    const products = await Product.find({
      danh_muc,
      trang_thai: "active",
    });
    console.log(`GET /api/product/category/${danh_muc} - Found ${products.length} products`);
    res.json(products);
  } catch (err) {
    console.error("Error in getProductsByCategory:", err);
    res.status(500).json({ error: err.message });
  }
};

// Lấy sản phẩm bán chạy
exports.getBestSellingProducts = async (req, res) => {
  try {
    const limit = Number(req.query.limit) || 10;
    const products = await Product.find({ trang_thai: "active" })
      .sort({ so_luong_da_ban: -1 })
      .limit(limit);
    console.log(`GET /api/product/best-selling - Found ${products.length} products`);
    res.json(products);
  } catch (err) {
    console.error("Error in getBestSellingProducts:", err);
    res.status(500).json({ error: err.message });
  }
};

// Lấy sản phẩm mới nhất
exports.getNewestProducts = async (req, res) => {
  try {
    const limit = Number(req.query.limit) || 10;
    const products = await Product.find({ trang_thai: "active" })
      .sort({ createdAt: -1 })
      .limit(limit);
    res.json(products);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};


