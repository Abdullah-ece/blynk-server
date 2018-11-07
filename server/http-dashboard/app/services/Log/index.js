const Log = {};

Log.instance = console;

Log.error = function () {
  Log.instance.error.apply({}, arguments);
};

export {
  Log
};

export default Log;
