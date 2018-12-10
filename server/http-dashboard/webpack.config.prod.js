// For info about this file refer to webpack and webpack-hot-middleware documentation
// For info on how we're generating bundles with hashed filenames for cache busting: https://medium.com/@okonetchnikov/long-term-caching-of-static-assets-with-webpack-1ecb139adb95#.w99i89nsz
/* eslint-disable no-console */
import webpack from 'webpack';
import ExtractTextPlugin from 'extract-text-webpack-plugin';
import WebpackMd5Hash from 'webpack-md5-hash';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import UglifyJsPlugin from 'uglifyjs-webpack-plugin';
import autoprefixer from 'autoprefixer';
import moment from "moment";
import path from 'path';

const commitHash = require('child_process')
  .execSync('git rev-parse --short HEAD')
  .toString();

const commitDate = require('child_process')
  .execSync(`git log -1 --format=%cd --date=format:"%H:%M %d.%m.%y"`)
  .toString();

const GLOBALS = {
  'process.env.NODE_ENV': JSON.stringify('production'),
  'process.env.BLYNK_ANALYTICS': JSON.stringify(process.env.BLYNK_ANALYTICS || false), // Defines need for analytics tab displayed inside the Admin navigation
  'process.env.BLYNK_POWERED_BY': JSON.stringify(process.env.BLYNK_POWERED_BY || false), // Defines need to display 'Powered By Blink' text inside the Admin
  'process.env.BLYNK_WATERMARK': JSON.stringify(process.env.BLYNK_WATERMARK || false), // Defines need to display Watermark in the right bottom of the screen inside the Admin dashboard layout
  'process.env.BLYNK_COMMIT_HASH': JSON.stringify(commitHash),
  'process.env.BLYNK_COMMIT_DATE': JSON.stringify(commitDate),
  'process.env.BLYNK_DEPLOYMENT_DATE': JSON.stringify(moment().format('HH:mm DD.MM.YYYY')),
  __DEV__: false,
  'process.env.BLYNK_CREATE_DEVICE': JSON.stringify(process.env.BLYNK_CREATE_DEVICE || true), // Allows device creation through the UI
};

console.log(GLOBALS);

export default {
  externals: {
    'google': 'google'
  },
  resolve: {
    extensions: ['*', '.js', '.jsx', '.json'],
    modules: [
      path.resolve('./app'),
      path.resolve('./node_modules')
    ]
  },
  devtool: 'source-map', // more info:https://webpack.github.io/docs/build-performance.html#sourcemaps and https://webpack.github.io/docs/configuration.html#devtool
  entry: path.resolve(__dirname, 'app/index'),
  target: 'web', // necessary per https://webpack.github.io/docs/testing.html#compile-and-test
  output: {
    path: path.resolve(__dirname, 'src/main/resources/static'),
    publicPath: '/static/',
    filename: '[name].[chunkhash].js'
  },
  recordsPath: path.join(__dirname, 'records.json'),
  plugins: [
    // Hash the files using MD5 so that their names change when the content changes.
    new WebpackMd5Hash(),

    // Tells React to build in prod mode. https://facebook.github.io/react/downloads.html
    new webpack.DefinePlugin(GLOBALS),

    // Generate an external css file with a hash in the filename
    new ExtractTextPlugin('[name].[contenthash].css'),

    // Generate HTML file that contains references to generated bundles. See here for how this works: https://github.com/ampedandwired/html-webpack-plugin#basic-usage
    new HtmlWebpackPlugin({
      template: 'app/index.ejs',
      minify: {
        removeComments: true,
        collapseWhitespace: true,
        removeRedundantAttributes: true,
        useShortDoctype: true,
        removeEmptyAttributes: true,
        removeStyleLinkTypeAttributes: true,
        keepClosingSlash: true,
        minifyJS: false,
        minifyCSS: true,
        minifyURLs: true
      },
      inject: true,
      // Note that you can add custom options here if you need to handle other custom logic in index.html
      // To track JavaScript errors via TrackJS, sign up for a free trial at TrackJS.com and enter your token below.
      trackJSToken: ''
    }),

    // Minify JS
    new UglifyJsPlugin({ cache: true, parallel: true, sourceMap: true }),

    new webpack.LoaderOptionsPlugin({
      minimize: false,
      debug: false,
      noInfo: true, // set to false to see a list of every file being bundled.
      options: {
        sassLoader: {
          includePaths: [path.resolve(__dirname, 'app', 'scss')]
        },
        context: '/',
        postcss: () => [autoprefixer],
      }
    })
  ],
  module: {
    noParse: [/^canvasjs$/gi],
    rules: [
      {
        test: /\.jsx?$/,
        loader: 'string-replace-loader',
        options: {
          multiple: [
            {
              search: '%(built_date)s',
              replace: `Build hash: ${moment().format("HHmmssDDMM")}`,
            },
            {
              search: '%(qa_watermark)',
              replace: "",
            },
          ]
        }
      },
      {
        test: /\.jsx?$/,
        exclude: /node_modules/,
        loader: 'babel-loader',
        options: 'cacheDirectory'
      },
      {
        test: /\.eot(\?v=\d+.\d+.\d+)?$/,
        loader: 'url-loader?name=[name].[ext]'
      },
      {
        test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        loader: 'url-loader?limit=10000&mimetype=application/font-woff&name=[name].[ext]'
      },
      {
        test: /\.[ot]tf(\?v=\d+.\d+.\d+)?$/,
        loader: 'url-loader?limit=10000&mimetype=application/octet-stream&name=[name].[ext]'
      },
      {
        test: /\.svg(\?v=\d+.\d+.\d+)?$/,
        loader: 'url-loader?limit=10000&mimetype=image/svg+xml&name=[name].[ext]'
      },
      { test: /\.(jpe?g|png|gif)$/i, loader: 'file-loader?name=[name].[ext]' },
      { test: /\.ico$/, loader: 'file-loader?name=[name].[ext]' },
      {
        test: /(\.css|\.less)$/,
        loaders: ExtractTextPlugin.extract({
          fallback: 'style-loader',
          use: [{
            loader: 'css-loader'
          }, {
            loader: 'postcss-loader'
          }, {
            loader: 'less-loader',
            options: {
              paths: [
                path.resolve(__dirname, "app"),
                path.resolve(__dirname, "node_modules"),
              ]
            }
          }],
        })
      }
    ]
  }
};
