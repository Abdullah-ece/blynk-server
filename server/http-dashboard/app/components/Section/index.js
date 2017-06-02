import React from 'react';
import './styles.less';

class Section extends React.Component {
  static propTypes = {
    title: React.PropTypes.string,
    children: React.PropTypes.any,
  };

  render() {
    return (
      <div className="content-section" {...this.props}>
        <div className="content-section--name">{this.props.title}</div>
        {this.props.children}
      </div>
    );
  }

}

export default Section;
