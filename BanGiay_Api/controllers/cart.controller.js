const Cart = require("../models/Cart");
const Product = require("../models/Product");

// Thêm sản phẩm vào giỏ hàng
exports.addToCart = async (req, res) => {
  try {
    console.log("\n========== ADD TO CART REQUEST ==========");
    console.log("Timestamp:", new Date().toISOString());
    console.log("Request Method:", req.method);
    console.log("Request URL:", req.originalUrl);
    console.log("Request Body:", JSON.stringify(req.body, null, 2));
    console.log("Request Headers:", JSON.stringify(req.headers, null, 2));
    
    const { user_id, product_id, size, quantity } = req.body;

    // Validation chi tiết
    console.log("\n--- VALIDATION ---");
    console.log("user_id:", user_id, "Type:", typeof user_id);
    console.log("product_id:", product_id, "Type:", typeof product_id);
    console.log("size:", size, "Type:", typeof size);
    console.log("quantity:", quantity, "Type:", typeof quantity);

    if (!user_id || !product_id || !size || quantity === undefined || quantity === null) {
      console.error("❌ VALIDATION FAILED - Missing required fields");
      return res.status(400).json({
        success: false,
        message: "Thiếu thông tin bắt buộc: user_id, product_id, size, quantity",
      });
    }

    if (quantity <= 0) {
      console.error("❌ VALIDATION FAILED - Quantity <= 0");
      return res.status(400).json({
        success: false,
        message: "Số lượng phải lớn hơn 0",
      });
    }

    // Kiểm tra sản phẩm có tồn tại không
    const productId = product_id || req.body.san_pham_id;
    console.log("\n--- PRODUCT LOOKUP ---");
    console.log("Looking for product with ID:", productId);
    
    const product = await Product.findById(productId);
    if (!product) {
      console.error("❌ PRODUCT NOT FOUND with ID:", productId);
      // Kiểm tra xem có sản phẩm nào trong DB không
      const productCount = await Product.countDocuments();
      console.log("Total products in database:", productCount);
      return res.status(404).json({
        success: false,
        message: `Sản phẩm không tồn tại (ID: ${productId})`,
      });
    }
    console.log("✅ Product found:");
    console.log("  - Name:", product.ten_san_pham);
    console.log("  - Price (original):", product.gia_goc);
    console.log("  - Price (sale):", product.gia_khuyen_mai);

    // Lấy giá sản phẩm (ưu tiên giá khuyến mãi)
    const price = product.gia_khuyen_mai || product.gia_goc;
    console.log("  - Using price:", price);

    // Tìm hoặc tạo cart cho user
    console.log("\n--- CART LOOKUP/CREATE ---");
    console.log("Looking for cart with user_id:", user_id);
    let cart = await Cart.findOne({ user_id });

    if (!cart) {
      console.log("Cart not found, creating new cart...");
      cart = new Cart({
        user_id,
        items: [],
      });
      console.log("✅ New cart created");
    } else {
      console.log("✅ Existing cart found. Cart ID:", cart._id);
      console.log("Current items count:", cart.items.length);
    }

    // Kiểm tra xem sản phẩm với size này đã có trong giỏ chưa
    console.log("\n--- CHECK EXISTING ITEM ---");
    const existingItemIndex = cart.items.findIndex(
      (item) =>
        item.san_pham_id.toString() === productId.toString() &&
        item.kich_thuoc === size
    );

    if (existingItemIndex !== -1) {
      // Nếu đã có, tăng số lượng
      console.log("Item already exists at index:", existingItemIndex);
      console.log("Current quantity:", cart.items[existingItemIndex].so_luong);
      cart.items[existingItemIndex].so_luong += quantity;
      cart.items[existingItemIndex].gia = price; // Cập nhật giá mới nhất
      console.log("New quantity:", cart.items[existingItemIndex].so_luong);
    } else {
      // Nếu chưa có, thêm mới
      console.log("Adding new item to cart...");
      cart.items.push({
        san_pham_id: productId,
        so_luong: quantity,
        kich_thuoc: size,
        gia: price,
      });
      console.log("✅ New item added. Total items:", cart.items.length);
    }

    // Lưu cart vào MongoDB
    console.log("\n--- SAVING TO MONGODB ---");
    await cart.save();
    console.log("✅ Cart saved successfully!");
    console.log("Cart ID:", cart._id);
    console.log("User ID:", cart.user_id);
    console.log("Total items in cart:", cart.items.length);
    console.log("Cart items:", JSON.stringify(cart.items, null, 2));

    // Verify cart was saved
    const savedCart = await Cart.findById(cart._id);
    if (savedCart) {
      console.log("✅ VERIFIED: Cart exists in MongoDB");
      console.log("Saved cart items count:", savedCart.items.length);
    } else {
      console.error("❌ ERROR: Cart was not saved to MongoDB!");
    }

    console.log("\n========== RESPONSE ==========");
    const response = {
      success: true,
      message: "Đã thêm sản phẩm vào giỏ hàng",
      data: cart,
    };
    console.log("Response:", JSON.stringify(response, null, 2));
    console.log("==========================================\n");

    res.json(response);
  } catch (err) {
    console.error("\n❌❌❌ ERROR IN addToCart ❌❌❌");
    console.error("Error message:", err.message);
    console.error("Error stack:", err.stack);
    console.error("Error name:", err.name);
    if (err.errors) {
      console.error("Validation errors:", JSON.stringify(err.errors, null, 2));
    }
    console.error("==========================================\n");
    
    res.status(500).json({
      success: false,
      message: err.message || "Lỗi server khi thêm vào giỏ hàng",
      error: err.message,
    });
  }
};

// Lấy giỏ hàng của user
exports.getCart = async (req, res) => {
  try {
    // Lấy user_id từ query params hoặc từ body
    const user_id = req.query.user_id || req.body.user_id || req.user?.id;

    if (!user_id) {
      return res.status(400).json({
        success: false,
        message: "Thiếu user_id",
      });
    }

    const cart = await Cart.findOne({ user_id }).populate(
      "items.san_pham_id",
      "ten_san_pham gia_goc gia_khuyen_mai hinh_anh"
    );

    if (!cart) {
      return res.json({
        success: true,
        data: {
          user_id,
          items: [],
        },
      });
    }

    res.json({
      success: true,
      data: cart,
    });
  } catch (err) {
    console.error("Lỗi khi lấy giỏ hàng:", err);
    res.status(500).json({
      success: false,
      error: err.message || "Lỗi server khi lấy giỏ hàng",
    });
  }
};

// Cập nhật số lượng sản phẩm trong giỏ hàng
exports.updateCartItem = async (req, res) => {
  try {
    const { user_id, product_id, size, quantity } = req.body;

    if (!user_id || !product_id || !size || quantity === undefined) {
      return res.status(400).json({
        success: false,
        message: "Thiếu thông tin: user_id, product_id, size, quantity",
      });
    }

    if (quantity <= 0) {
      return res.status(400).json({
        success: false,
        message: "Số lượng phải lớn hơn 0",
      });
    }

    const cart = await Cart.findOne({ user_id });
    if (!cart) {
      return res.status(404).json({
        success: false,
        message: "Giỏ hàng không tồn tại",
      });
    }

    // Tìm item trong cart
    const itemIndex = cart.items.findIndex(
      (item) =>
        item.san_pham_id.toString() === product_id &&
        item.kich_thuoc === size
    );

    if (itemIndex === -1) {
      return res.status(404).json({
        success: false,
        message: "Sản phẩm không có trong giỏ hàng",
      });
    }

    // Cập nhật số lượng
    cart.items[itemIndex].so_luong = quantity;

    // Cập nhật giá mới nhất
    const product = await Product.findById(product_id);
    if (product) {
      cart.items[itemIndex].gia = product.gia_khuyen_mai || product.gia_goc;
    }

    await cart.save();

    res.json({
      success: true,
      message: "Đã cập nhật số lượng",
      cart,
    });
  } catch (err) {
    console.error("Lỗi khi cập nhật giỏ hàng:", err);
    res.status(500).json({
      success: false,
      error: err.message || "Lỗi server khi cập nhật giỏ hàng",
    });
  }
};

// Xóa sản phẩm khỏi giỏ hàng
exports.removeFromCart = async (req, res) => {
  try {
    const { user_id, product_id, size } = req.body;

    if (!user_id || !product_id || !size) {
      return res.status(400).json({
        success: false,
        message: "Thiếu thông tin: user_id, product_id, size",
      });
    }

    const cart = await Cart.findOne({ user_id });
    if (!cart) {
      return res.status(404).json({
        success: false,
        message: "Giỏ hàng không tồn tại",
      });
    }

    // Xóa item khỏi array
    cart.items = cart.items.filter(
      (item) =>
        !(
          item.san_pham_id.toString() === product_id &&
          item.kich_thuoc === size
        )
    );

    await cart.save();

    res.json({
      success: true,
      message: "Đã xóa sản phẩm khỏi giỏ hàng",
      cart,
    });
  } catch (err) {
    console.error("Lỗi khi xóa khỏi giỏ hàng:", err);
    res.status(500).json({
      success: false,
      error: err.message || "Lỗi server khi xóa khỏi giỏ hàng",
    });
  }
};

