import React from 'react';
import classnames from 'classnames';

class Legend extends React.Component {

  static propTypes = {
    children: React.PropTypes.any,
    type: React.PropTypes.string
  };

  render() {

    const className = classnames({
      "fieldset--legend": true,
    });

    return (
      <div className={className}>{this.props.children}</div>
    );
  }

}

export default Legend;
