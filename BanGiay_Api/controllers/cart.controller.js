const Cart = require("../models/Cart");
const Product = require("../models/Product");

/**
 * Thêm sản phẩm vào giỏ hàng
 * POST /api/cart
 * Body: { user_id, product_id, size, quantity }
 */
exports.addToCart = async (req, res) => {
  try {
    const { user_id, product_id, size, quantity } = req.body;

    // Validation
    if (!user_id) {
      return res.status(400).json({
        success: false,
        message: "Thiếu user_id",
      });
    }

    if (!product_id) {
      return res.status(400).json({
        success: false,
        message: "Thiếu product_id",
      });
    }

    if (!size || size.trim() === "") {
      return res.status(400).json({
        success: false,
        message: "Thiếu kích thước (size)",
      });
    }

    if (!quantity || quantity <= 0) {
      return res.status(400).json({
        success: false,
        message: "Số lượng phải lớn hơn 0",
      });
    }

    // Kiểm tra sản phẩm có tồn tại không
    const product = await Product.findById(product_id);
    if (!product) {
      return res.status(404).json({
        success: false,
        message: `Sản phẩm không tồn tại (ID: ${product_id})`,
      });
    }

    // Kiểm tra size có hợp lệ không
    if (!product.kich_thuoc || !product.kich_thuoc.includes(size)) {
      return res.status(400).json({
        success: false,
        message: `Kích thước ${size} không có sẵn cho sản phẩm này`,
      });
    }

    // Kiểm tra số lượng tồn kho
    if (product.so_luong_ton < quantity) {
      return res.status(400).json({
        success: false,
        message: `Số lượng tồn kho không đủ. Chỉ còn ${product.so_luong_ton} sản phẩm`,
      });
    }

    // Lấy giá sản phẩm (ưu tiên giá khuyến mãi)
    const price = product.gia_khuyen_mai || product.gia_goc;

    // Tìm hoặc tạo cart cho user
    let cart = await Cart.findOne({ user_id });

    if (!cart) {
      cart = new Cart({
        user_id,
        items: [],
      });
    }

    // Kiểm tra xem sản phẩm với size này đã có trong giỏ chưa
    const existingItemIndex = cart.items.findIndex(
      (item) =>
        item.san_pham_id.toString() === product_id.toString() &&
        item.kich_thuoc === size
    );

    if (existingItemIndex !== -1) {
      // Nếu đã có, tăng số lượng
      const newQuantity = cart.items[existingItemIndex].so_luong + quantity;
      
      // Kiểm tra lại số lượng tồn kho
      if (product.so_luong_ton < newQuantity) {
        return res.status(400).json({
          success: false,
          message: `Số lượng tồn kho không đủ. Chỉ còn ${product.so_luong_ton} sản phẩm`,
        });
      }

      cart.items[existingItemIndex].so_luong = newQuantity;
      cart.items[existingItemIndex].gia = price; // Cập nhật giá mới nhất
    } else {
      // Nếu chưa có, thêm mới
      cart.items.push({
        san_pham_id: product_id,
        so_luong: quantity,
        kich_thuoc: size,
        gia: price,
      });
    }

    // Lưu cart vào MongoDB
    await cart.save();

    // Populate product info để trả về
    await cart.populate("items.san_pham_id", "ten_san_pham gia_goc gia_khuyen_mai hinh_anh thuong_hieu danh_muc");

    res.json({
      success: true,
      message: "Đã thêm sản phẩm vào giỏ hàng",
      data: cart,
    });
  } catch (err) {
    console.error("Lỗi khi thêm vào giỏ hàng:", err);
    res.status(500).json({
      success: false,
      message: err.message || "Lỗi server khi thêm vào giỏ hàng",
    });
  }
};

/**
 * Lấy giỏ hàng của user
 * GET /api/cart?user_id=xxx
 */
exports.getCart = async (req, res) => {
  try {
    const user_id = req.query.user_id || req.body.user_id;

    if (!user_id) {
      return res.status(400).json({
        success: false,
        message: "Thiếu user_id",
      });
    }

    const cart = await Cart.findOne({ user_id }).populate({
      path: "items.san_pham_id",
      select: "ten_san_pham gia_goc gia_khuyen_mai hinh_anh mo_ta thuong_hieu danh_muc danh_gia kich_thuoc so_luong_ton _id",
    });

    // Nếu không có cart, trả về cart rỗng
    if (!cart) {
      console.log("Cart not found for user:", user_id);
      return res.json({
        success: true,
        data: {
          user_id,
          items: [],
        },
      });
    }

    console.log("Cart found for user:", user_id);
    console.log("Cart items count:", cart.items.length);
    if (cart.items.length > 0) {
      console.log("First item product:", cart.items[0].san_pham_id);
      console.log("First item product type:", typeof cart.items[0].san_pham_id);
    }

    res.json({
      success: true,
      data: cart,
    });
  } catch (err) {
    console.error("Lỗi khi lấy giỏ hàng:", err);
    res.status(500).json({
      success: false,
      message: err.message || "Lỗi server khi lấy giỏ hàng",
    });
  }
};

/**
 * Cập nhật số lượng sản phẩm trong giỏ hàng
 * PUT /api/cart/item
 * Body: { user_id, product_id, size, quantity }
 */
exports.updateCartItem = async (req, res) => {
  try {
    const { user_id, product_id, size, quantity } = req.body;

    // Validation
    if (!user_id || !product_id || !size || quantity === undefined || quantity === null) {
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

    // Tìm cart
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
        item.san_pham_id.toString() === product_id.toString() &&
        item.kich_thuoc === size
    );

    if (itemIndex === -1) {
      return res.status(404).json({
        success: false,
        message: "Sản phẩm không có trong giỏ hàng",
      });
    }

    // Kiểm tra sản phẩm và số lượng tồn kho
    const product = await Product.findById(product_id);
    if (!product) {
      return res.status(404).json({
        success: false,
        message: "Sản phẩm không tồn tại",
      });
    }

    if (product.so_luong_ton < quantity) {
      return res.status(400).json({
        success: false,
        message: `Số lượng tồn kho không đủ. Chỉ còn ${product.so_luong_ton} sản phẩm`,
      });
    }

    // Cập nhật số lượng và giá
    cart.items[itemIndex].so_luong = quantity;
    cart.items[itemIndex].gia = product.gia_khuyen_mai || product.gia_goc;

    await cart.save();

    // Populate product info
    await cart.populate("items.san_pham_id", "ten_san_pham gia_goc gia_khuyen_mai hinh_anh thuong_hieu danh_muc");

    res.json({
      success: true,
      message: "Đã cập nhật số lượng",
      data: cart,
    });
  } catch (err) {
    console.error("Lỗi khi cập nhật giỏ hàng:", err);
    res.status(500).json({
      success: false,
      message: err.message || "Lỗi server khi cập nhật giỏ hàng",
    });
  }
};

/**
 * Xóa sản phẩm khỏi giỏ hàng
 * DELETE /api/cart/item
 * Body: { user_id, product_id, size }
 */
exports.removeFromCart = async (req, res) => {
  try {
    const { user_id, product_id, size } = req.body;

    // Validation
    if (!user_id || !product_id || !size) {
      return res.status(400).json({
        success: false,
        message: "Thiếu thông tin: user_id, product_id, size",
      });
    }

    // Tìm cart
    const cart = await Cart.findOne({ user_id });
    if (!cart) {
      return res.status(404).json({
        success: false,
        message: "Giỏ hàng không tồn tại",
      });
    }

    // Xóa item khỏi array
    const initialLength = cart.items.length;
    cart.items = cart.items.filter(
      (item) =>
        !(
          item.san_pham_id.toString() === product_id.toString() &&
          item.kich_thuoc === size
        )
    );

    if (cart.items.length === initialLength) {
      return res.status(404).json({
        success: false,
        message: "Sản phẩm không có trong giỏ hàng",
      });
    }

    await cart.save();

    // Populate product info
    await cart.populate("items.san_pham_id", "ten_san_pham gia_goc gia_khuyen_mai hinh_anh thuong_hieu danh_muc");

    res.json({
      success: true,
      message: "Đã xóa sản phẩm khỏi giỏ hàng",
      data: cart,
    });
  } catch (err) {
    console.error("Lỗi khi xóa khỏi giỏ hàng:", err);
    res.status(500).json({
      success: false,
      message: err.message || "Lỗi server khi xóa khỏi giỏ hàng",
    });
  }
};

/**
 * Xóa toàn bộ giỏ hàng
 * DELETE /api/cart
 * Body: { user_id }
 */
exports.clearCart = async (req, res) => {
  try {
    const { user_id } = req.body;

    if (!user_id) {
      return res.status(400).json({
        success: false,
        message: "Thiếu user_id",
      });
    }

    const cart = await Cart.findOne({ user_id });
    if (!cart) {
      return res.status(404).json({
        success: false,
        message: "Giỏ hàng không tồn tại",
      });
    }

    cart.items = [];
    await cart.save();

    res.json({
      success: true,
      message: "Đã xóa toàn bộ giỏ hàng",
      data: cart,
    });
  } catch (err) {
    console.error("Lỗi khi xóa giỏ hàng:", err);
    res.status(500).json({
      success: false,
      message: err.message || "Lỗi server khi xóa giỏ hàng",
    });
  }
};
