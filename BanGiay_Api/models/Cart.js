const mongoose = require("mongoose");

const CartItemSchema = new mongoose.Schema({
  san_pham_id: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Product",
    required: true,
  },
  so_luong: { 
    type: Number, 
    required: true, 
    min: 1,
    validate: {
      validator: Number.isInteger,
      message: "Số lượng phải là số nguyên"
    }
  },
  kich_thuoc: { 
    type: String, 
    required: true,
    trim: true
  },
  gia: { 
    type: Number, 
    required: true,
    min: 0
  }, // Giá tại thời điểm thêm vào giỏ
}, { _id: true });

const CartSchema = new mongoose.Schema(
  {
    user_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
      unique: true,
      index: true,
    },
    items: {
      type: [CartItemSchema],
      default: []
    },
  },
  { 
    timestamps: true,
    toJSON: { virtuals: true },
    toObject: { virtuals: true }
  }
);

// Virtual để tính tổng tiền
CartSchema.virtual("tong_tien").get(function() {
  return this.items.reduce((total, item) => {
    return total + (item.gia * item.so_luong);
  }, 0);
});

// Virtual để đếm tổng số lượng sản phẩm
CartSchema.virtual("tong_so_luong").get(function() {
  return this.items.reduce((total, item) => {
    return total + item.so_luong;
  }, 0);
});

module.exports = mongoose.model("Cart", CartSchema);
