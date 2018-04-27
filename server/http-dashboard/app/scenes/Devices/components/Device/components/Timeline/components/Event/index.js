import React          from 'react';
import Simple         from './simple';
import Resolved       from './resolved';
import './styles.less';
import {EVENT_TYPES} from "services/Products/index";
import Idle from './idle';


class Event extends React.Component {

  static propTypes = {
    event: React.PropTypes.object,
    params: React.PropTypes.object,
    onMarkAsResolved: React.PropTypes.func
  };

  render() {
    if(this.props.event.eventType === EVENT_TYPES.WAS_OFFLINE){
      return (<Idle event={this.props.event} />);
    }
    if (this.props.event.isResolved)
      return (<Resolved event={this.props.event}/>);

    return (<Simple event={this.props.event} params={this.props.params}
                    onMarkAsResolved={this.props.onMarkAsResolved.bind(this)}/>);
  }

}

export default Event;
