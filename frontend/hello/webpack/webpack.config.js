const path = require('path');
const CopyPlugin = require('copy-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

// Костыль, вебпак генерит js с css - https://github.com/webpack-contrib/mini-css-extract-plugin/issues/151
class Without {
    constructor(patterns) {
        this.patterns = patterns;
    }

    apply(compiler) {
        compiler.hooks.emit.tapAsync("MiniCssExtractPluginCleanup", (compilation, callback) => {
            Object.keys(compilation.assets)
                .filter(asset => {
                    let match = false,
                        i = this.patterns.length
                    ;
                    while (i--) {
                        if (this.patterns[i].test(asset)) {
                            match = true;
                        }
                    }
                    return match;
                }).forEach(asset => {
                delete compilation.assets[asset];
            });

            callback();
        });
    }
}


module.exports = {
    mode: 'development',
    resolve: {
        extensions: ['.js', '.scss', '.css', 'sass']
    },
    entry: {
        bootstrap: './src/bootstrap.js',
        'css/dark.harbor' : './src/scss/dark.harbor.scss',
        'css/default.harbor' : './src/scss/default.harbor.scss'
    },
    output: {
        path: path.resolve(__dirname, '../../webpack-output/hello/assets/webpack'),
        filename: '[name].js',
        publicPath: '/hello/assets/webpack/'
    },
    module: {
        rules: [{
            test: /\.(sa|sc|c)ss$/,
            use: [
                MiniCssExtractPlugin.loader,
                {
                    loader: 'css-loader'
                },
                {
                    loader: 'sass-loader',
                    options: {
                        sourceMap: true,
                        // options...
                    }
                }
            ]
        }]
    },
    plugins: [
        new CopyPlugin([
            {
                from: path.resolve(__dirname, '../build/gwt/war/hello/'),
                to: path.resolve(__dirname, '../../webpack-output/hello/assets/webpack/gwt')
            },
        ]),
        new MiniCssExtractPlugin({
            filename: "[name].css",
            chunkFilename: "[name].css"
        }),
        new Without([/(dark.harbor|default.harbor)\.js(\.map)?$/]), // just give a list with regex patterns that should be excluded
    ]
};