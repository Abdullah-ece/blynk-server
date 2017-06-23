import React from 'react';
import {Button} from 'antd';
import {Widgets} from 'components';
import './styles.less';

class Dashboard extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      filter: this.FILTERS.HOUR,
      editable: false
    };
  }

  FILTERS = {
    HOUR: 'hour',
    DAY: 'day',
    WEEK: 'week',
    MONTH: 'month',
    CUSTOM: 'custom'
  };

  FILTER_BUTTONS = [{
    key: this.FILTERS.HOUR,
    value: '1 hour'
  }, {
    key: this.FILTERS.DAY,
    value: '1 day'
  }, {
    key: this.FILTERS.WEEK,
    value: '1 Week'
  }, {
    key: this.FILTERS.MONTH,
    value: 'Month'
  }, {
    key: this.FILTERS.CUSTOM,
    value: 'Custom Range'
  }];

  filterBy(key) {
    this.setState({
      filter: key
    });
  }

  startEditDashboard() {
    this.setState({
      editable: true
    });
  }

  finishEditDashboard() {
    this.setState({
      editable: false
    });
  }

  render() {
    return (
      <div>

        <div>
          <Button.Group size="default" className="devices-device-dashboard-time-filter">
            { this.FILTER_BUTTONS.map((button, key) => (
              <Button key={key}
                      onClick={this.filterBy.bind(this, button.key)}
                      type={button.key === this.state.filter && 'primary' || 'default'}>
                {button.value}
              </Button>
            ))}
          </Button.Group>
          <Button.Group className="dashboard-tools">
            { this.state.editable && (
              <Button icon="check" onClick={this.finishEditDashboard.bind(this)}/>
            )}
            { !this.state.editable && (
              <Button icon="tool" className="transparent" onClick={this.startEditDashboard.bind(this)}/>
            )}

          </Button.Group>
        </div>

        <Widgets editable={this.state.editable}/>

      </div>
    );
  }

}

export default Dashboard;
