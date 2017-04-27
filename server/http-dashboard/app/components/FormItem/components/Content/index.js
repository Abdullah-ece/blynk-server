import React from 'react';
import classnames from 'classnames';

class Content extends React.Component {
  static propTypes = {
    children: React.PropTypes.any,
    input: React.PropTypes.any
  };

  render() {

    const classNames = classnames({
      'form-item-content': true,
      'input': !!this.props.input
    });

    return (
      <div className={classNames}>
        {this.props.children}
      </div>
    );
  }
}

export default Content;
