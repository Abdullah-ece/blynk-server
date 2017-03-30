const path = require('path');
const webpack = require('webpack');

process.noDeprecation = true

module.exports = {
    context: path.resolve(__dirname, './src/main/webapp'),
    entry: {
        app: './js/app.js',
        login: './js/test.js',
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: [/node_modules/],
                use: [{
                    loader: 'babel-loader',
                    options: {presets: ['es2015', 'react']}
                }],
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader'],
            },
            {
                test: /\.less$/,
                use: [{
                    loader: "style-loader" // creates style nodes from JS strings
                }, {
                    loader: "css-loader" // translates CSS into CommonJS
                }, {
                    loader: "less-loader" // compiles Less to CSS
                }]
            }
        ],
    },
    output: {
        path: path.resolve(__dirname, './src/main/resources/static'),
        filename: '[name].bundle.js',
        publicPath: '/static/',
    },
    devServer: {
        contentBase: path.resolve(__dirname, './src/main/webapp'),
        proxy: {
            '/dashboard': {
                target: 'http://localhost:8080/',
                secure: false
            }
        }
    },
};
