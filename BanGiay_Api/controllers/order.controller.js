const Order = require("../models/Order");
const Cart = require("../models/Cart");
const Product = require("../models/Product");

// Tạo đơn hàng mới từ giỏ hàng
exports.createOrder = async (req, res) => {
  try {
    const { user_id, items, tong_tien, dia_chi_giao_hang, so_dien_thoai, ghi_chu } = req.body;

    // Validation
    if (!user_id || !items || !Array.isArray(items) || items.length === 0) {
      return res.status(400).json({
        success: false,
        message: "Thiếu thông tin bắt buộc: user_id, items",
      });
    }

    if (!tong_tien || tong_tien <= 0) {
      return res.status(400).json({
        success: false,
        message: "Tổng tiền phải lớn hơn 0",
      });
    }

    // Tính lại tổng tiền từ items để đảm bảo chính xác
    let calculatedTotal = 0;
    for (const item of items) {
      if (!item.gia || !item.so_luong) {
        return res.status(400).json({
          success: false,
          message: "Thiếu thông tin sản phẩm: gia, so_luong",
        });
      }
      calculatedTotal += item.gia * item.so_luong;
    }

    // Tạo đơn hàng mới
    const newOrder = new Order({
      user_id,
      items,
      tong_tien: calculatedTotal,
      trang_thai: "pending", // Mặc định là chờ xác nhận
      dia_chi_giao_hang: dia_chi_giao_hang || "",
      so_dien_thoai: so_dien_thoai || "",
      ghi_chu: ghi_chu || "",
    });

    await newOrder.save();

    // Xóa giỏ hàng sau khi tạo đơn hàng thành công
    await Cart.findOneAndDelete({ user_id });

    res.status(201).json({
      success: true,
      message: "Đơn hàng đã được tạo thành công",
      data: newOrder,
    });
  } catch (err) {
    console.error("Lỗi khi tạo đơn hàng:", err);
    res.status(500).json({
      success: false,
      error: err.message || "Lỗi server khi tạo đơn hàng",
    });
  }
};

// Lấy danh sách đơn hàng của user
exports.getOrders = async (req, res) => {
  try {
    const { user_id, trang_thai } = req.query;

    if (!user_id) {
      return res.status(400).json({
        success: false,
        message: "Thiếu user_id",
      });
    }

    // Xây dựng query
    const query = { user_id };
    if (trang_thai) {
      query.trang_thai = trang_thai;
    }

    const orders = await Order.find(query)
      .sort({ createdAt: -1 }) // Mới nhất trước
      .populate("items.san_pham_id", "ten_san_pham hinh_anh");

    res.json({
      success: true,
      data: orders,
    });
  } catch (err) {
    console.error("Lỗi khi lấy danh sách đơn hàng:", err);
    res.status(500).json({
      success: false,
      error: err.message || "Lỗi server khi lấy danh sách đơn hàng",
    });
  }
};

// Lấy chi tiết đơn hàng
exports.getOrderById = async (req, res) => {
  try {
    const { orderId } = req.params;

    const order = await Order.findById(orderId).populate(
      "items.san_pham_id",
      "ten_san_pham hinh_anh gia_goc gia_khuyen_mai"
    );

    if (!order) {
      return res.status(404).json({
        success: false,
        message: "Đơn hàng không tồn tại",
      });
    }

    res.json({
      success: true,
      data: order,
    });
  } catch (err) {
    console.error("Lỗi khi lấy chi tiết đơn hàng:", err);
    if (err.name === "CastError") {
      return res.status(400).json({
        success: false,
        message: "ID đơn hàng không hợp lệ",
      });
    }
    res.status(500).json({
      success: false,
      error: err.message || "Lỗi server khi lấy chi tiết đơn hàng",
    });
  }
};

// Cập nhật trạng thái đơn hàng
exports.updateOrderStatus = async (req, res) => {
  try {
    const { orderId } = req.params;
    const { trang_thai } = req.body;

    if (!trang_thai) {
      return res.status(400).json({
        success: false,
        message: "Thiếu trạng thái",
      });
    }

    const validStatuses = ["pending", "confirmed", "shipping", "delivered", "cancelled"];
    if (!validStatuses.includes(trang_thai)) {
      return res.status(400).json({
        success: false,
        message: "Trạng thái không hợp lệ. Các trạng thái hợp lệ: " + validStatuses.join(", "),
      });
    }

    const order = await Order.findByIdAndUpdate(
      orderId,
      { trang_thai },
      { new: true, runValidators: true }
    );

    if (!order) {
      return res.status(404).json({
        success: false,
        message: "Đơn hàng không tồn tại",
      });
    }

    res.json({
      success: true,
      message: "Đã cập nhật trạng thái đơn hàng",
      data: order,
    });
  } catch (err) {
    console.error("Lỗi khi cập nhật trạng thái đơn hàng:", err);
    res.status(500).json({
      success: false,
      error: err.message || "Lỗi server khi cập nhật trạng thái đơn hàng",
    });
  }
};

// Hủy đơn hàng
exports.cancelOrder = async (req, res) => {
  try {
    const { orderId } = req.params;

    const order = await Order.findById(orderId);

    if (!order) {
      return res.status(404).json({
        success: false,
        message: "Đơn hàng không tồn tại",
      });
    }

    // Chỉ cho phép hủy đơn hàng ở trạng thái pending hoặc confirmed
    if (order.trang_thai === "delivered") {
      return res.status(400).json({
        success: false,
        message: "Không thể hủy đơn hàng đã giao",
      });
    }

    if (order.trang_thai === "cancelled") {
      return res.status(400).json({
        success: false,
        message: "Đơn hàng đã được hủy trước đó",
      });
    }

    order.trang_thai = "cancelled";
    await order.save();

    res.json({
      success: true,
      message: "Đã hủy đơn hàng",
      data: order,
    });
  } catch (err) {
    console.error("Lỗi khi hủy đơn hàng:", err);
    res.status(500).json({
      success: false,
      error: err.message || "Lỗi server khi hủy đơn hàng",
    });
  }
};

