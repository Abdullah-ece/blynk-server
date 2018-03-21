import React          from 'react';
import Simple         from './simple';
import Resolved       from './resolved';
import './styles.less';

class Event extends React.Component {

  static propTypes = {
    event: React.PropTypes.object,
    params: React.PropTypes.object,
    onMarkAsResolved: React.PropTypes.func
  };

  render() {
    if (this.props.event.isResolved)
      return (<Resolved event={this.props.event}/>);

    return (<Simple event={this.props.event} params={this.props.params}
                    onMarkAsResolved={this.props.onMarkAsResolved.bind(this)}/>);
  }

}

export default Event;
