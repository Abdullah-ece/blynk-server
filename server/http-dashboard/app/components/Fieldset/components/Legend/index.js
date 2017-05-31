import React from 'react';

class Legend extends React.Component {

  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="fieldset--legend">{this.props.children}</div>
    );
  }

}

export default Legend;
