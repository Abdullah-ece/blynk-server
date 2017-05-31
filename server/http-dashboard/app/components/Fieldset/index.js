import React from 'react';
import {Legend} from './components';
import './styles.less';

class Fieldset extends React.Component {

  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="fieldset">{this.props.children}</div>
    );
  }

}

Fieldset.Legend = Legend;

export default Fieldset;
