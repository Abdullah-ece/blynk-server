import React from 'react';
import {ContentEditable} from 'components';
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
      <ContentEditable value={value} onChange={onChange}/>
    );
  }

}

export default DeviceName;
