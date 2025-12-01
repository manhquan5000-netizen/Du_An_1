const mongoose = require("mongoose");
const connectDB = require("./config/db");
const Product = require("./models/Product");
require("dotenv").config();

// Dữ liệu 10 sản phẩm mẫu
const sampleProducts = [
  {
    ten_san_pham: "Giày Thể Thao Nike Air Max 270",
    gia_goc: 3500000,
    gia_khuyen_mai: 2800000,
    hinh_anh: "https://example.com/images/nike-air-max-270.jpg",
    mo_ta: "Giày thể thao Nike Air Max 270 với công nghệ đệm khí tiên tiến, phù hợp cho chạy bộ và thể thao hàng ngày. Chất liệu da tổng hợp bền bỉ, đế cao su chống trượt.",
    thuong_hieu: "Nike",
    danh_muc: "nam",
    kich_thuoc: ["40", "41", "42", "43", "44"],
    so_luong_ton: 50,
    danh_gia: 4.5,
    so_luong_da_ban: 120,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Cao Gót Nữ Jimmy Choo",
    gia_goc: 8500000,
    gia_khuyen_mai: 6800000,
    hinh_anh: "https://example.com/images/jimmy-choo-heels.jpg",
    mo_ta: "Giày cao gót sang trọng với thiết kế thanh lịch, phù hợp cho các dịp đặc biệt. Chất liệu da thật cao cấp, đế cao 10cm với đệm êm ái.",
    thuong_hieu: "Jimmy Choo",
    danh_muc: "nu",
    kich_thuoc: ["36", "37", "38", "39"],
    so_luong_ton: 25,
    danh_gia: 4.8,
    so_luong_da_ban: 45,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Converse Chuck Taylor All Star",
    gia_goc: 1500000,
    gia_khuyen_mai: 1200000,
    hinh_anh: "https://example.com/images/converse-chuck-taylor.jpg",
    mo_ta: "Giày Converse cổ điển với thiết kế unisex, phù hợp mọi lứa tuổi. Chất liệu vải canvas bền, đế cao su cổ điển, dễ phối đồ.",
    thuong_hieu: "Converse",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 100,
    danh_gia: 4.6,
    so_luong_da_ban: 250,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Chạy Bộ Adidas Ultraboost 22",
    gia_goc: 4200000,
    gia_khuyen_mai: 3500000,
    hinh_anh: "https://example.com/images/adidas-ultraboost.jpg",
    mo_ta: "Giày chạy bộ Adidas Ultraboost với công nghệ Boost đệm khí, mang lại cảm giác êm ái và hỗ trợ tối đa khi chạy. Phù hợp cho marathon và chạy đường dài.",
    thuong_hieu: "Adidas",
    danh_muc: "unisex",
    kich_thuoc: ["38", "39", "40", "41", "42", "43", "44"],
    so_luong_ton: 60,
    danh_gia: 4.7,
    so_luong_da_ban: 180,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Búp Bê Nữ Bitis Hunter",
    gia_goc: 1200000,
    gia_khuyen_mai: 950000,
    hinh_anh: "https://example.com/images/bitis-hunter.jpg",
    mo_ta: "Giày búp bê thời trang với thiết kế trẻ trung, năng động. Chất liệu da tổng hợp dễ vệ sinh, đế cao su chống trượt, phù hợp đi học và đi chơi.",
    thuong_hieu: "Bitis",
    danh_muc: "nu",
    kich_thuoc: ["35", "36", "37", "38", "39"],
    so_luong_ton: 80,
    danh_gia: 4.4,
    so_luong_da_ban: 200,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Tây Nam Công Sở Clarks",
    gia_goc: 2800000,
    gia_khuyen_mai: 2200000,
    hinh_anh: "https://example.com/images/clarks-dress-shoes.jpg",
    mo_ta: "Giày tây công sở với thiết kế thanh lịch, chuyên nghiệp. Chất liệu da thật cao cấp, đế da mềm mại, phù hợp mặc vest và quần tây.",
    thuong_hieu: "Clarks",
    danh_muc: "nam",
    kich_thuoc: ["40", "41", "42", "43", "44", "45"],
    so_luong_ton: 40,
    danh_gia: 4.5,
    so_luong_da_ban: 95,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Sneaker Vans Old Skool",
    gia_goc: 1800000,
    gia_khuyen_mai: 1500000,
    hinh_anh: "https://example.com/images/vans-old-skool.jpg",
    mo_ta: "Giày sneaker Vans với thiết kế cổ điển, phong cách streetwear. Chất liệu vải canvas và da tổng hợp, đế waffle cổ điển, phù hợp giới trẻ.",
    thuong_hieu: "Vans",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 70,
    danh_gia: 4.6,
    so_luong_da_ban: 160,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Boot Nam Timberland",
    gia_goc: 4500000,
    gia_khuyen_mai: 3800000,
    hinh_anh: "https://example.com/images/timberland-boot.jpg",
    mo_ta: "Giày boot cao cổ với thiết kế mạnh mẽ, chống nước tốt. Chất liệu da thật bền bỉ, đế cao su chống trượt, phù hợp đi phượt và công việc ngoài trời.",
    thuong_hieu: "Timberland",
    danh_muc: "nam",
    kich_thuoc: ["40", "41", "42", "43", "44"],
    so_luong_ton: 35,
    danh_gia: 4.7,
    so_luong_da_ban: 75,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Sandal Nữ Birkenstock",
    gia_goc: 2200000,
    gia_khuyen_mai: 1800000,
    hinh_anh: "https://example.com/images/birkenstock-sandal.jpg",
    mo_ta: "Giày sandal với đế nút chai tự nhiên, thiết kế theo hình dáng bàn chân. Mang lại cảm giác thoải mái, phù hợp đi biển và mùa hè.",
    thuong_hieu: "Birkenstock",
    danh_muc: "nu",
    kich_thuoc: ["36", "37", "38", "39", "40"],
    so_luong_ton: 55,
    danh_gia: 4.5,
    so_luong_da_ban: 110,
    trang_thai: "active",
  },
  {
    ten_san_pham: "Giày Thể Thao Puma RS-X",
    gia_goc: 3200000,
    gia_khuyen_mai: 2600000,
    hinh_anh: "https://example.com/images/puma-rsx.jpg",
    mo_ta: "Giày thể thao Puma với thiết kế retro hiện đại, công nghệ đệm RS. Chất liệu da tổng hợp và mesh, đế cao su bền, phù hợp tập gym và thể thao.",
    thuong_hieu: "Puma",
    danh_muc: "unisex",
    kich_thuoc: ["38", "39", "40", "41", "42", "43"],
    so_luong_ton: 65,
    danh_gia: 4.6,
    so_luong_da_ban: 140,
    trang_thai: "active",
  },
];

// Hàm seed dữ liệu
const seedProducts = async () => {
  try {
    // Kết nối database
    await connectDB();

    // Xóa tất cả sản phẩm cũ (tùy chọn)
    // await Product.deleteMany({});
    // console.log("Đã xóa dữ liệu cũ");

    // Insert sản phẩm mới
    const products = await Product.insertMany(sampleProducts);
    console.log(`✅ Đã thêm thành công ${products.length} sản phẩm vào MongoDB!`);

    // Hiển thị danh sách sản phẩm đã thêm
    console.log("\n📦 Danh sách sản phẩm đã thêm:");
    products.forEach((product, index) => {
      console.log(
        `${index + 1}. ${product.ten_san_pham} - ${product.gia_khuyen_mai.toLocaleString("vi-VN")} VNĐ`
      );
    });

    process.exit(0);
  } catch (error) {
    console.error("❌ Lỗi khi seed dữ liệu:", error);
    process.exit(1);
  }
};

// Chạy seed
seedProducts();


