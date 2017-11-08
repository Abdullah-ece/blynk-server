import React from 'react';

import './styles.less';

class Section extends React.Component {

  static propTypes = {
    title: React.PropTypes.string,
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="user-profile--section">
        <div className="user-profile--section-title">{ this.props.title }</div>
        { this.props.children }
      </div>
    );
  }
}

export default Section;

export Item from './components/Item';
export {
  Section
};
