import React from 'react';

class Content extends React.Component {
  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="form-item-title-group">
        {this.props.children}
      </div>
    );
  }
}

export default Content;
