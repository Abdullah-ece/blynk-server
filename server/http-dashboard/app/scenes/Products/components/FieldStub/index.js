import React from 'react';
import './styles.less';
import Dotdotdot from 'react-dotdotdot';

class FieldStub extends React.Component {

  static propTypes = {
    multipleLines: React.PropTypes.bool,
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className={`product-metadata-static-field ${!this.props.children && 'no-value'}`}
           style={{wordWrap: 'break-word'}}>
        { !this.props.multipleLines && (
          <Dotdotdot clamp={1}>
            { this.props.children || 'No Value' }
          </Dotdotdot>
        ) || (
          <p>{ this.props.children || 'No Value' }</p>
        )}
      </div>
    );
  }

}

export default FieldStub;
