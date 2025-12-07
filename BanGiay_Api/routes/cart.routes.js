const express = require("express");
const router = express.Router();
const CartController = require("../controllers/cart.controller");

// POST /api/cart - Thêm sản phẩm vào giỏ hàng
router.post("/", CartController.addToCart);

// GET /api/cart - Lấy giỏ hàng của user (cần user_id trong query)
router.get("/", CartController.getCart);

// PUT /api/cart/item - Cập nhật số lượng sản phẩm trong giỏ hàng
router.put("/item", CartController.updateCartItem);

// DELETE /api/cart/item - Xóa sản phẩm khỏi giỏ hàng
router.delete("/item", CartController.removeFromCart);

module.exports = router;

