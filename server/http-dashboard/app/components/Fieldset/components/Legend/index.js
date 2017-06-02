import React from 'react';
import classnames from 'classnames';

class Legend extends React.Component {

  static propTypes = {
    children: React.PropTypes.any,
    type: React.PropTypes.string
  };

  render() {

    const DARK = 'dark';

    const className = classnames({
      "fieldset--legend": true,
      "fieldset--legend--dark": this.props.type === DARK
    });

    return (
      <div className={className}>{this.props.children}</div>
    );
  }

}

export default Legend;
