import React from 'react';

import './styles.less';

import ReactFontAwesome from 'react-fontawesome';

class FontAwesome extends React.Component {

  render() {
    return (
      <ReactFontAwesome {...this.props}/>
    );
  }

}

export default FontAwesome;
