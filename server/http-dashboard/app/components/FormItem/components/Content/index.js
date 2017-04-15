import React from 'react';

class Content extends React.Component {
  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="form-item-content">
        {this.props.children}
      </div>
    );
  }
}

export default Content;
