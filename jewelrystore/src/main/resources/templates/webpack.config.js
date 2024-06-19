const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const fs = require("fs");

module.exports = {
  entry: "./src/index.html",
  output: {
    path: path.resolve(__dirname, "dist"),
    filename: "index.html",
  },
  module: {
    rules: [
      {
        test: /\.html$/i,
        loader: "html-loader",
      },
    ],
  },
  plugins: [
    new HtmlWebpackPlugin({
      templateContent: ({ htmlWebpackPlugin }) => {
        const header = fs.readFileSync("./src/header.html", "utf-8");
        return htmlWebpackPlugin.files.html.replace("<!-- header -->", header);
      },
      filename: "index.html",
    }),
  ],
};
