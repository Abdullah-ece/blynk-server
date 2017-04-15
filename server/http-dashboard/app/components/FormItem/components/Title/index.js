import React from 'react';

class Title extends React.Component {
  static propTypes = {
    children: React.PropTypes.any,
    style: React.PropTypes.any
  };

  render() {
    return (
      <div className="form-item-title" style={this.props.style}>
        {this.props.children}
      </div>
    );
  }
}

export default Title;
