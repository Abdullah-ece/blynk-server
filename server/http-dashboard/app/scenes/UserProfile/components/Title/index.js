import React from 'react';

import './styles.scss';

class Title extends React.Component {

  static propTypes = {
    text: React.PropTypes.string
  };

  render() {
    const {text} = this.props;
    return (
      <div className="user-profile--title">{text}</div>
    );
  }
}

export default Title;
