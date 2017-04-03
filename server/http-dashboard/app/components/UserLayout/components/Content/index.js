import React from 'react';

import './styles.scss';

class Content extends React.Component {

  static propTypes = {
    children: React.PropTypes.object
  };

  render() {

    return (
      <div className="user-layout--content">
        { this.props.children }
      </div>
    );
  }

}

export default Content;
