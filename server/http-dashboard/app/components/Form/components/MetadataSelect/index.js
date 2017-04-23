import React from 'react';

import {Field as FormField} from 'redux-form';
import Select from './select';

export default class Field extends React.Component {

  render() {
    const props = this.props;
    return (
      <FormField {...props} component={Select}/>
    );
  }
}
