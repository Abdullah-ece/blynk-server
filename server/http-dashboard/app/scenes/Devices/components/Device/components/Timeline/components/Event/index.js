import React          from 'react';
import Simple         from './simple';
import Resolved       from './resolved';
import './styles.less';

class Event extends React.Component {

  static propTypes = {
    event: React.PropTypes.object
  };

  render() {
    if (this.props.event.get('isResolved'))
      return <Resolved event={this.props.event}/>;

    return <Simple event={this.props.event}/>;
  }

}

export default Event;
