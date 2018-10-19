import React from 'react';
import {ContentEditableInput} from 'components';
import PropTypes from 'prop-types';

class DeviceName extends React.Component {

  static propTypes = {
    value: PropTypes.string,

    onChange: PropTypes.func,
  };

  render() {

    const {
      value,
      onChange
    } = this.props;

    return (
      <ContentEditableInput maxLength={40} value={value} onChange={onChange}/>
    );
  }

}

export default DeviceName;
