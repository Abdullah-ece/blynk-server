import React                              from 'react';

import moment from 'moment';

class Idle extends React.Component {

  static propTypes = {
    event: React.PropTypes.object,
  };

  render() {

    if (this.props.event.ts === 0)
      return null;

    let absentTime = "Offline for ";

    if(this.props.event.ts / 1000 / 60 > 1){
      absentTime += moment.duration(this.props.event.ts / 1000, "seconds").format("d[d] h[h] m[min]");
    } else {
      absentTime += Math.floor(this.props.event.ts / 1000) + " seconds";
    }

    return (
      <div className="devices--device-timeline--info">
        {absentTime}
      </div>
    );
  }

}

export default Idle;
