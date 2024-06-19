const gulp = require("gulp");
const fileInclude = require("gulp-file-include");
const del = require("del");
const path = require("path");

// Đường dẫn đến jQuery từ node_modules
const jqueryPath = path.join(
  __dirname,
  "node_modules",
  "jquery",
  "dist",
  "jquery.min.js"
);

// Đường dẫn đích đến thư mục src/js
const destPath = path.join(__dirname, "src", "js");

// Tạo một task để sao chép jQuery
gulp.task("copy-jquery", function () {
  return gulp.src(jqueryPath).pipe(gulp.dest(destPath));
});
// Task để xóa thư mục 'dist'
function clean() {
  return del(["dist"]);
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
    .pipe(gulp.dest("./dist"));
});

// Task để sao chép file CSS
gulp.task("copy-css", function () {
  return gulp.src("src/css/**/*.css").pipe(gulp.dest("dist/css"));
});

// Task để sao chép hình ảnh
gulp.task("copy-images", function () {
  return gulp.src("src/images/**/*").pipe(gulp.dest("dist/images"));
});

// Task để sao chép file JS
gulp.task("copy-js", function () {
  return gulp.src("src/js/**/*.js").pipe(gulp.dest("dist/js"));
});

// Task để theo dõi thay đổi
gulp.task("watch", function () {
  gulp.watch(["src/**/*.html"], gulp.series("file-include")); // Theo dõi các thay đổi trong thư mục 'src'
  gulp.watch("src/css/**/*.css", gulp.series("copy-css")); // Theo dõi các thay đổi trong thư mục 'css'
  gulp.watch("src/images/**/*", gulp.series("copy-images")); // Theo dõi các thay đổi trong thư mục 'images'
  gulp.watch("src/js/**/*.js", gulp.series("copy-js")); // Theo dõi các thay đổi trong thư mục 'js'
});

// Task để build dự án
gulp.task(
  "build",
  gulp.series(
    "clean",
    "file-include",
    "copy-jquery",
    "copy-js",
    "copy-css",
    "copy-images"
  )
);

// Task mặc định
gulp.task("default", gulp.series("build"));
