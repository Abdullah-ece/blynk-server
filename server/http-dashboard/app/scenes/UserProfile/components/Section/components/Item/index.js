import React from 'react';

import './styles.scss';

class Item extends React.Component {

  static propTypes = {
    title: React.PropTypes.string
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
