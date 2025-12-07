const express = require("express");
const router = express.Router();
const OrderController = require("../controllers/order.controller");

// POST /api/order - Tạo đơn hàng mới
router.post("/", OrderController.createOrder);

// GET /api/order - Lấy danh sách đơn hàng của user
router.get("/", OrderController.getOrders);

// GET /api/order/:orderId - Lấy chi tiết đơn hàng
router.get("/:orderId", OrderController.getOrderById);

// PUT /api/order/:orderId/status - Cập nhật trạng thái đơn hàng
router.put("/:orderId/status", OrderController.updateOrderStatus);

// PUT /api/order/:orderId/cancel - Hủy đơn hàng
router.put("/:orderId/cancel", OrderController.cancelOrder);

module.exports = router;

