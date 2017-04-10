import React from 'react';

import './styles.less';

class Item extends React.Component {

  static propTypes = {
    title: React.PropTypes.string,
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="user-profile--section--item">
        <div className="user-profile--section--item-title">
          { this.props.title }
        </div>
        <div className="user-profile--section--item-content">
          { this.props.children }
        </div>
      </div>
    );
  }

}

export default Item;
