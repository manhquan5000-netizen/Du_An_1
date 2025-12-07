const express = require("express");
const router = express.Router();
const CartController = require("../controllers/cart.controller");

/**
 * Cart Routes
 * Base path: /api/cart
 */

// POST /api/cart - Thêm sản phẩm vào giỏ hàng
router.post("/", CartController.addToCart);

// GET /api/cart?user_id=xxx - Lấy giỏ hàng của user
router.get("/", CartController.getCart);

// PUT /api/cart/item - Cập nhật số lượng sản phẩm trong giỏ hàng
router.put("/item", CartController.updateCartItem);

// DELETE /api/cart/item - Xóa sản phẩm khỏi giỏ hàng
router.delete("/item", CartController.removeFromCart);

// DELETE /api/cart - Xóa toàn bộ giỏ hàng
router.delete("/", CartController.clearCart);

module.exports = router;

