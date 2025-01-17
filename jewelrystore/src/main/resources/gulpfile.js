const gulp = require("gulp");
const fileInclude = require("gulp-file-include");
const del = require("del");
const path = require("path");
const replace = require("gulp-replace");
const dotenv = require("dotenv");

// Load environment variables from .env file
dotenv.config();

gulp.task("replace-env", function () {
  return gulp
    .src("src/js/**/*.js") // Adjust the glob pattern to match your JS files
    .pipe(replace("process.env.API_URL", JSON.stringify(process.env.API_URL)))
    .pipe(gulp.dest("static/js")); // Output folder for the processed files
});
// Đường dẫn đến jQuery từ node_modules
const jqueryPath = path.join(
  __dirname,
  "node_modules",
  "jquery",
  "dist",
  "jquery.min.js"
);
const jbarcodePath = path.join(
  __dirname,
  "node_modules",
  "jsbarcode",
  "dist",
  "JsBarcode.all.min.js"
);
const jqueryUiPath = path.join(
  __dirname,
  "node_modules",
  "jquery-ui",
  "dist",
  "jquery-ui.min.js"
);
const flowbitePath = path.join(
  __dirname,
  "node_modules",
  "flowbite",
  "dist",
  "flowbite.min.js"
);
gulp.task("copy-flowbite", function () {
  return gulp.src(flowbitePath).pipe(gulp.dest(destPath));
});
// Đường dẫn đích đến thư mục src/js
const destPath = path.join(__dirname, "src", "js");

// Tạo một task để sao chép jQuery
gulp.task("copy-jquery", function () {
  return gulp.src(jqueryPath).pipe(gulp.dest(destPath));
});
gulp.task("copy-jquery-ui", function () {
  return gulp.src(jqueryUiPath).pipe(gulp.dest(destPath));
});
// Task để xóa thư mục 'dist'
function clean() {
  return del(["static"]);
}
gulp.task("copy-jbarcode", function () {
  return gulp.src(jbarcodePath).pipe(gulp.dest(destPath));
});
// Task để xóa thư mục 'dist'
function clean() {
  return del(["static"]);
}
gulp.task("clean", clean);

// Task để xử lý include HTML
gulp.task("file-include", function () {
  return gulp
    .src(["src/*.html"])
    .pipe(
      fileInclude({
        prefix: "@@",
        basepath: "@file",
      })
    )
    .pipe(gulp.dest("./templates"));
});

// Task để sao chép file CSS
gulp.task("copy-css", function () {
  return gulp.src("src/css/**/*.css").pipe(gulp.dest("static/css"));
});

// Task để sao chép hình ảnh
gulp.task("copy-images", function () {
  return gulp.src("src/images/**/*").pipe(gulp.dest("static/images"));
});

// Task để theo dõi thay đổi
gulp.task("watch", function () {
  gulp.watch(["src/**/*.html"], gulp.series("file-include")); // Theo dõi các thay đổi trong thư mục 'src'
  gulp.watch("src/css/**/*.css", gulp.series("copy-css")); // Theo dõi các thay đổi trong thư mục 'css'
  gulp.watch("src/images/**/*", gulp.series("copy-images")); // Theo dõi các thay đổi trong thư mục 'images'
  gulp.watch("src/js/**/*.js", gulp.series("replace-env")); // Theo dõi các thay đổi trong thư mục 'js'
});

// Task để build dự án
gulp.task(
  "build",
  gulp.series(
    "clean",
    "file-include",
    "copy-jquery-ui",
    "copy-jbarcode",
    "copy-jquery",
    "replace-env",
    "copy-css",
    "copy-images"
  )
);

// Task mặc định
gulp.task("default", gulp.series("build"));
