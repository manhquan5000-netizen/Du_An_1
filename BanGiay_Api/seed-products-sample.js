const mongoose = require("mongoose");
const Product = require("./models/Product");
require("dotenv").config();

// Kết nối MongoDB
const connectDB = async () => {
  try {
    await mongoose.connect(process.env.MONGODB_URI || "mongodb://localhost:27017/ban_giay", {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });
    console.log("✅ Đã kết nối MongoDB");
  } catch (error) {
    console.error("❌ Lỗi kết nối MongoDB:", error);
    process.exit(1);
  }
};

// Dữ liệu sản phẩm mẫu
const products = [
  {
    ten_san_pham: "Giày Converse Chuck Taylor All Star",
    gia_goc: 1500000,
    gia_khuyen_mai: 1200000,
    hinh_anh: "giay14.img",
    mo_ta: "Giày Converse cổ điển với thiết kế unisex, phù hợp mọi lứa tuổi. Chất liệu vải canvas bền, đế cao su cổ điển, dễ phối đồ.",
    thuong_hieu: "Converse",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 100,
    danh_gia: 4.6,
    so_luong_da_ban: 250,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Nike Air Max 270",
    gia_goc: 3500000,
    gia_khuyen_mai: 2800000,
    hinh_anh: "nike_airmax_270.img",
    mo_ta: "Giày thể thao Nike Air Max 270 với công nghệ Air cushioning, đệm êm ái, phù hợp cho chạy bộ và đi bộ hàng ngày.",
    thuong_hieu: "Nike",
    danh_muc: "unisex",
    kich_thuoc: ["38", "39", "40", "41", "42", "43", "44"],
    so_luong_ton: 75,
    danh_gia: 4.8,
    so_luong_da_ban: 320,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Adidas Ultraboost 22",
    gia_goc: 4200000,
    gia_khuyen_mai: 3500000,
    hinh_anh: "adidas_ultraboost.img",
    mo_ta: "Giày chạy bộ Adidas Ultraboost với công nghệ Boost, đệm năng lượng cao cấp, phù hợp cho vận động viên.",
    thuong_hieu: "Adidas",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 60,
    danh_gia: 4.9,
    so_luong_da_ban: 180,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Vans Old Skool Classic",
    gia_goc: 1800000,
    gia_khuyen_mai: 1500000,
    hinh_anh: "vans_oldskool.img",
    mo_ta: "Giày Vans Old Skool với thiết kế cổ điển, phù hợp cho giới trẻ, dễ phối đồ street style.",
    thuong_hieu: "Vans",
    danh_muc: "unisex",
    kich_thuoc: ["36", "37", "38", "39", "40", "41", "42"],
    so_luong_ton: 120,
    danh_gia: 4.7,
    so_luong_da_ban: 450,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Puma Suede Classic",
    gia_goc: 2000000,
    gia_khuyen_mai: 1600000,
    hinh_anh: "puma_suede.img",
    mo_ta: "Giày Puma Suede với chất liệu da lộn cao cấp, thiết kế retro, phù hợp mọi phong cách.",
    thuong_hieu: "Puma",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 90,
    danh_gia: 4.5,
    so_luong_da_ban: 280,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Nike Air Force 1 Low",
    gia_goc: 3200000,
    gia_khuyen_mai: 2500000,
    hinh_anh: "nike_af1.img",
    mo_ta: "Giày Nike Air Force 1 với thiết kế iconic, phù hợp cho street style, chất liệu da cao cấp.",
    thuong_hieu: "Nike",
    danh_muc: "unisex",
    kich_thuoc: ["38", "39", "40", "41", "42", "43", "44"],
    so_luong_ton: 85,
    danh_gia: 4.8,
    so_luong_da_ban: 520,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Adidas Stan Smith",
    gia_goc: 2500000,
    gia_khuyen_mai: 2000000,
    hinh_anh: "adidas_stansmith.img",
    mo_ta: "Giày Adidas Stan Smith với thiết kế minimalist, phù hợp cho phong cách casual, chất liệu da thật.",
    thuong_hieu: "Adidas",
    danh_muc: "unisex",
    kich_thuoc: ["36", "37", "38", "39", "40", "41", "42"],
    so_luong_ton: 110,
    danh_gia: 4.6,
    so_luong_da_ban: 380,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Converse One Star",
    gia_goc: 1800000,
    gia_khuyen_mai: 1400000,
    hinh_anh: "converse_onestar.img",
    mo_ta: "Giày Converse One Star với thiết kế đơn giản, phù hợp cho phong cách retro, dễ phối đồ.",
    thuong_hieu: "Converse",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 95,
    danh_gia: 4.5,
    so_luong_da_ban: 220,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Nike Dunk Low",
    gia_goc: 3800000,
    gia_khuyen_mai: 3000000,
    hinh_anh: "nike_dunk.img",
    mo_ta: "Giày Nike Dunk với thiết kế cổ điển, phù hợp cho skateboarding và street style, chất liệu da cao cấp.",
    thuong_hieu: "Nike",
    danh_muc: "unisex",
    kich_thuoc: ["38", "39", "40", "41", "42", "43", "44"],
    so_luong_ton: 70,
    danh_gia: 4.7,
    so_luong_da_ban: 150,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Vans Authentic",
    gia_goc: 1600000,
    gia_khuyen_mai: 1300000,
    hinh_anh: "vans_authentic.img",
    mo_ta: "Giày Vans Authentic với thiết kế đơn giản, phù hợp cho giới trẻ, dễ phối đồ casual.",
    thuong_hieu: "Vans",
    danh_muc: "unisex",
    kich_thuoc: ["36", "37", "38", "39", "40", "41", "42"],
    so_luong_ton: 130,
    danh_gia: 4.6,
    so_luong_da_ban: 410,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Adidas Superstar",
    gia_goc: 2800000,
    gia_khuyen_mai: 2200000,
    hinh_anh: "adidas_superstar.img",
    mo_ta: "Giày Adidas Superstar với thiết kế iconic, phù hợp cho phong cách hip-hop, chất liệu da cao cấp.",
    thuong_hieu: "Adidas",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 100,
    danh_gia: 4.8,
    so_luong_da_ban: 350,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Puma RS-X",
    gia_goc: 3200000,
    gia_khuyen_mai: 2600000,
    hinh_anh: "puma_rsx.img",
    mo_ta: "Giày Puma RS-X với thiết kế futuristic, phù hợp cho phong cách street style, công nghệ đệm hiện đại.",
    thuong_hieu: "Puma",
    danh_muc: "unisex",
    kich_thuoc: ["38", "39", "40", "41", "42", "43", "44"],
    so_luong_ton: 80,
    danh_gia: 4.6,
    so_luong_da_ban: 190,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Nike React Element 55",
    gia_goc: 4000000,
    gia_khuyen_mai: 3200000,
    hinh_anh: "nike_react.img",
    mo_ta: "Giày Nike React với công nghệ React foam, đệm êm ái, phù hợp cho chạy bộ và đi bộ hàng ngày.",
    thuong_hieu: "Nike",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 65,
    danh_gia: 4.9,
    so_luong_da_ban: 140,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Converse Chuck 70",
    gia_goc: 2200000,
    gia_khuyen_mai: 1800000,
    hinh_anh: "converse_chuck70.img",
    mo_ta: "Giày Converse Chuck 70 với chất liệu cao cấp hơn, đế dày hơn, phù hợp cho những người yêu thích phong cách cổ điển.",
    thuong_hieu: "Converse",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 88,
    danh_gia: 4.7,
    so_luong_da_ban: 210,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Vans Sk8-Hi",
    gia_goc: 2000000,
    gia_khuyen_mai: 1600000,
    hinh_anh: "vans_sk8hi.img",
    mo_ta: "Giày Vans Sk8-Hi với thiết kế cổ cao, phù hợp cho skateboarding, bảo vệ mắt cá chân tốt.",
    thuong_hieu: "Vans",
    danh_muc: "unisex",
    kich_thuoc: ["36", "37", "38", "39", "40", "41", "42"],
    so_luong_ton: 105,
    danh_gia: 4.6,
    so_luong_da_ban: 290,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Adidas NMD R1",
    gia_goc: 4500000,
    gia_khuyen_mai: 3600000,
    hinh_anh: "adidas_nmd.img",
    mo_ta: "Giày Adidas NMD với công nghệ Boost, thiết kế hiện đại, phù hợp cho phong cách street style cao cấp.",
    thuong_hieu: "Adidas",
    danh_muc: "unisex",
    kich_thuoc: ["38", "39", "40", "41", "42", "43", "44"],
    so_luong_ton: 55,
    danh_gia: 4.8,
    so_luong_da_ban: 120,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Nike Blazer Mid",
    gia_goc: 3000000,
    gia_khuyen_mai: 2400000,
    hinh_anh: "nike_blazer.img",
    mo_ta: "Giày Nike Blazer với thiết kế cổ điển, phù hợp cho phong cách retro, chất liệu da cao cấp.",
    thuong_hieu: "Nike",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 92,
    danh_gia: 4.7,
    so_luong_da_ban: 260,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Puma Cali",
    gia_goc: 2400000,
    gia_khuyen_mai: 1900000,
    hinh_anh: "puma_cali.img",
    mo_ta: "Giày Puma Cali với thiết kế retro, phù hợp cho phong cách casual, chất liệu da lộn và da thật.",
    thuong_hieu: "Puma",
    danh_muc: "unisex",
    kich_thuoc: ["36", "37", "38", "39", "40", "41", "42"],
    so_luong_ton: 115,
    danh_gia: 4.5,
    so_luong_da_ban: 330,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Converse Jack Purcell",
    gia_goc: 1900000,
    gia_khuyen_mai: 1500000,
    hinh_anh: "converse_jackpurcell.img",
    mo_ta: "Giày Converse Jack Purcell với thiết kế tinh tế, phù hợp cho phong cách preppy, chất liệu canvas cao cấp.",
    thuong_hieu: "Converse",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 98,
    danh_gia: 4.6,
    so_luong_da_ban: 240,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Vans Era",
    gia_goc: 1700000,
    gia_khuyen_mai: 1400000,
    hinh_anh: "vans_era.img",
    mo_ta: "Giày Vans Era với thiết kế cổ điển, phù hợp cho skateboarding, có đệm ở cổ chân.",
    thuong_hieu: "Vans",
    danh_muc: "unisex",
    kich_thuoc: ["36", "37", "38", "39", "40", "41", "42"],
    so_luong_ton: 125,
    danh_gia: 4.6,
    so_luong_da_ban: 370,
    trang_thai: "active"
  },
  {
    ten_san_pham: "Giày Adidas Gazelle",
    gia_goc: 2600000,
    gia_khuyen_mai: 2100000,
    hinh_anh: "adidas_gazelle.img",
    mo_ta: "Giày Adidas Gazelle với thiết kế retro, phù hợp cho phong cách casual, chất liệu da lộn cao cấp.",
    thuong_hieu: "Adidas",
    danh_muc: "unisex",
    kich_thuoc: ["37", "38", "39", "40", "41", "42", "43"],
    so_luong_ton: 102,
    danh_gia: 4.7,
    so_luong_da_ban: 310,
    trang_thai: "active"
  }
];

// Hàm seed dữ liệu
const seedProducts = async () => {
  try {
    await connectDB();

    // Xóa tất cả sản phẩm cũ (tùy chọn)
    // await Product.deleteMany({});
    // console.log("✅ Đã xóa tất cả sản phẩm cũ");

    // Thêm sản phẩm mới
    const insertedProducts = await Product.insertMany(products);
    console.log(`✅ Đã thêm ${insertedProducts.length} sản phẩm vào database`);

    // Hiển thị danh sách sản phẩm đã thêm
    console.log("\n📦 Danh sách sản phẩm đã thêm:");
    insertedProducts.forEach((product, index) => {
      console.log(`${index + 1}. ${product.ten_san_pham} - ${product.thuong_hieu} - ${product.gia_khuyen_mai.toLocaleString('vi-VN')}₫`);
    });

    process.exit(0);
  } catch (error) {
    console.error("❌ Lỗi khi seed dữ liệu:", error);
    process.exit(1);
  }
};

// Chạy seed
seedProducts();

