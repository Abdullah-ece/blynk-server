import React                              from 'react';
import {convertTimeStampToTime} from 'services/Time';

class Idle extends React.Component {

  static propTypes = {
    event: React.PropTypes.object,
  };

  render() {

    if (this.props.event.ts === 0)
      return null;

    let absentTime = "Offline for " + convertTimeStampToTime(this.props.event.ts);

    return (
      <div className="devices--device-timeline--info">
        {absentTime}
      </div>
    );
  }

}

export default Idle;
