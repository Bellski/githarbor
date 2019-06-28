const path = require('path');
const CopyPlugin = require('copy-webpack-plugin');
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
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
        monaco : './src/monaco.bootstrap.js',
        'css/dark.harbor': './src/scss/dark.harbor.scss',
        'css/default.harbor': './src/scss/default.harbor.scss'
    },
    output: {
        path: path.resolve(__dirname, '../../webpack-output/harbor/assets/webpack'),
        filename: '[name].js',
        publicPath: '/harbor/assets/webpack/'
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: [
                            [
                                "env",
                                {modules: false}
                            ]
                        ],
                        plugins: [
                            [
                                "component",
                                {
                                    libraryName: "element-ui",
                                    styleLibraryName: "theme-chalk"
                                }
                            ]
                        ]
                    }
                }
            },
            {
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
            },
            {
                test: /.(ttf|otf|eot|woff(2)?)(\?[a-z0-9]+)?$/,
                use: [{
                    loader: 'file-loader',
                    options: {
                        name: '[name].[ext]',
                        outputPath: 'fonts/',
                        publicPath: '/harbor/assets/webpack/fonts'
                    }
                }]
            }
        ]
    },
    plugins: [
        new CopyPlugin([
            {
                from: path.resolve(__dirname, '../build/gwt/war/harbor/'),
                to: path.resolve(__dirname, '../../webpack-output/harbor/assets/webpack/gwt')
            }
        ]),
        new MiniCssExtractPlugin({
            filename: "[name].css",
            chunkFilename: "[name].css"
        }),
        new MonacoWebpackPlugin({
            features: [
                'contextmenu',
                'bracketMatching',
                'caretOperations',
                'clipboard',
                'goToDefinitionCommands',
                'goToDefinitionMouse',
                'hover',
                'links',
                'parameterHints',
                'referenceSearch',
                'wordHighlighter',
                'find'
            ]
        }),
        new Without([/(dark.harbor|default.harbor)\.js(\.map)?$/]), // just give a list with regex patterns that should be excluded
    ]
};