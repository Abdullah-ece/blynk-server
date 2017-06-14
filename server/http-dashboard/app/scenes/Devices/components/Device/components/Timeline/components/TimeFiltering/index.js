import React from 'react';
import {Field} from 'redux-form';
import {TimeSelect} from './components';
import './styles.less';

class TimeFiltering extends React.Component {

  render() {
    return (
      <Field {...this.props} component={TimeSelect}/>
    );
  }

}

export default TimeFiltering;
