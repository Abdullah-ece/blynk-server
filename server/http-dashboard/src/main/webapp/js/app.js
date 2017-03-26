import moment from 'moment';
import styles from '../less/app.less';
import 'antd/dist/antd.css';
import React from 'react';
import ReactDOM from 'react-dom';
import { DatePicker, LocaleProvider } from 'antd';
import enUS from 'antd/lib/locale-provider/en_US';

var rightNow = moment().format('MMMM Do YYYY, h:mm:ss a');
console.log(rightNow);

class HelloMessage extends React.Component {
  render() {
    return <div>Hello {this.props.name}
      <button>click me</button>
      <LocaleProvider locale={enUS}>
        <DatePicker/>
      </LocaleProvider>
    </div>;
  }
}

ReactDOM.render(<HelloMessage name="John" />, document.getElementById('app'));
