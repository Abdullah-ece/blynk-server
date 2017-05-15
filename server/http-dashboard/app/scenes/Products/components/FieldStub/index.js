import React from 'react';
import './styles.less';
import Dotdotdot from 'react-dotdotdot'

class FieldStub extends React.Component {

  render() {
    return (
      <div className={`product-metadata-static-field ${!this.props.children && 'no-value'}`}
           style={{wordWrap: 'break-word'}}>
        <Dotdotdot clamp={1}>
          { this.props.children || 'No Value' }
        </Dotdotdot>
      </div>
    );
  }

}

export default FieldStub;
