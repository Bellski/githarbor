const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyPlugin = require('copy-webpack-plugin');

module.exports = {
    mode: 'development',
    entry: {
        "js/bootstrap": './src/js/bootstrap.js'
    },
    output: {
        path: path.resolve(__dirname, '../../webpack-output/about/assets/webpack'),
        filename: '[name].js',
        publicPath: '/about/assets/webpack/'
    },
    module: {
        rules: [{
            test: /\.scss$/,
            use: [
                "style-loader", // creates style nodes from JS strings
                "css-loader", // translates CSS into CommonJS
                "sass-loader" // compiles Sass to CSS, using Node Sass by default
            ]
        }, {
            test: /\.(png|jpe?g|gif|svg)(\?.*)?$/,
            loader: 'url-loader',
            options: {
                limit: 10000
            }
        }]
    },
    plugins: [
        new CopyPlugin([
            {
                from: path.resolve(__dirname, './src/imgs/'),
                to: path.resolve(__dirname, '../../webpack-output/about/assets/webpack/imgs/')
            },
            {
                from: path.resolve(__dirname, './src/index.html'),
                to: path.resolve(__dirname, '../../webpack-output/about/assets/webpack')
            },
            {
                from: path.resolve(__dirname, './src/about.html'),
                to: path.resolve(__dirname, '../../webpack-output/about/assets/webpack')
            }
        ])
    ]
};