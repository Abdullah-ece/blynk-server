/* eslint-disable no-console */
import webpack from 'webpack';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import autoprefixer from 'autoprefixer';
import path from 'path';
import moment from "moment";

const commitHash = require('child_process')
  .execSync('git rev-parse --short HEAD')
  .toString();

const commitDate = require('child_process')
  .execSync(`git log -1 --format=%cd --date=format:"%H:%M %d.%m.%y"`)
  .toString();

const GLOBALS = {
  'process.env.NODE_ENV': JSON.stringify('development'), // Tells React to build in either dev or prod modes. https://facebook.github.io/react/downloads.html (See bottom)
  'process.env.BLYNK_ANALYTICS': JSON.stringify(process.env.BLYNK_ANALYTICS || false), // Defines need for analytics tab displayed inside the Admin navigation
  'process.env.BLYNK_POWERED_BY': JSON.stringify(process.env.BLYNK_POWERED_BY || false), // Defines need to display 'Powered By Blink' text inside the Admin
  'process.env.BLYNK_WATERMARK': JSON.stringify(process.env.BLYNK_WATERMARK || false), // Defines need to display Watermark in the right bottom of the screen inside the Admin dashboard layout
  'process.env.BLYNK_COMMIT_HASH': JSON.stringify(commitHash),
  'process.env.BLYNK_COMMIT_DATE': JSON.stringify(commitDate),
  'process.env.BLYNK_DEPLOYMENT_DATE': JSON.stringify(moment().format('HH:mm DD.MM.YYYY')),
  __DEV__: true,
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
  devtool: 'eval-source-map', // more info:https://webpack.github.io/docs/build-performance.html#sourcemaps and https://webpack.github.io/docs/configuration.html#devtool
  entry: [
    // must be first entry to properly set public path
    './app/webpack-public-path',
    'react-hot-loader/patch',
    'webpack-hot-middleware/client?reload=true',
    path.resolve(__dirname, 'app/index.js') // Defining path seems necessary for this to work consistently on Windows machines.
  ],
  target: 'web', // necessary per https://webpack.github.io/docs/testing.html#compile-and-test
  output: {
    path: path.resolve(__dirname, 'dist'), // Note: Physical files are only output by the production build task `npm run build`.
    publicPath: '/',
    filename: 'bundle.js'
  },
  plugins: [
    new webpack.DefinePlugin(GLOBALS),
    new webpack.HotModuleReplacementPlugin(),
    new webpack.NoEmitOnErrorsPlugin(),
    new HtmlWebpackPlugin({     // Create HTML file that includes references to bundled CSS and JS.
      template: 'app/index.ejs',
      minify: {
        removeComments: true,
        collapseWhitespace: true
      },
      inject: true
    }),
    new webpack.LoaderOptionsPlugin({
      minimize: false,
      debug: true,
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
    rules: [
      {
        test: /\.jsx?$/,
        loader: 'string-replace-loader',
        options: {
          multiple: [
            {
              search: '%(built_date)s',
              replace: "",
            }
          ]
        }
      },
      { test: /\.jsx?$/, exclude: /node_modules/, loaders: ['babel-loader'] },
      { test: /\.eot(\?v=\d+.\d+.\d+)?$/, loader: 'file-loader' },
      {
        test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        loader: 'url-loader?limit=10000&mimetype=application/font-woff'
      },
      {
        test: /\.[ot]tf(\?v=\d+.\d+.\d+)?$/,
        loader: 'url-loader?limit=10000&mimetype=application/octet-stream'
      },
      {
        test: /\.svg(\?v=\d+\.\d+\.\d+)?$/,
        loader: 'url-loader?limit=10000&mimetype=image/svg+xml'
      },
      { test: /\.(jpe?g|png|gif)$/i, loader: 'file-loader?name=[name].[ext]' },
      { test: /\.ico$/, loader: 'file-loader?name=[name].[ext]' },
      {
        test: /(\.css|\.less)$/,
        // loaders: ['style-loader', 'css-loader?sourceMap', 'postcss-loader', 'less-loader?sourceMap']
        use: [{
          loader: 'style-loader'
        }, {
          loader: 'css-loader?sourceMap'
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
        }]
      }
    ]
  }
};
