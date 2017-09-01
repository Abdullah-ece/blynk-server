import React from 'react';
import {Button} from 'antd';
import {Widgets} from 'components';
import './styles.less';
import {Map} from 'immutable';
import PropTypes from 'prop-types';

class Dashboard extends React.Component {

  static propTypes = {
    dashboard: PropTypes.instanceOf(Map),
    params: PropTypes.object,
  };

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
    value: 'Last hour'
  }, {
    key: this.FILTERS.DAY,
    value: 'Last day'
  }, {
    key: this.FILTERS.WEEK,
    value: 'Last Week'
  }, {
    key: this.FILTERS.MONTH,
    value: 'Last Month'
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

    let widgets;

    if (this.props.dashboard.has('widgets')) {

      widgets = {
        lg: this.props.dashboard.get('widgets').map((item) => {
          return ({
            ...item.toJS(),
            i: String(item.get('id')),
            id: String(item.get('id')),
            w: item.get('width'),
            h: item.get('height'),
            x: item.get('x'),
            y: item.get('y')
          });
        }).toJS()
      };
    } else {
      widgets = {
        lg: []
      };
    }

    if (!this.props.dashboard.has('widgets') || !this.props.dashboard.get('widgets').size)
      return (
        <div className="devices--device-dashboard">
          <div className="product-no-fields" style={{padding:0}}>No Dashboard widgets</div>
        </div>
      );

    return (
      <div className="devices--device-dashboard">

        <div>
          <Button.Group size="default" className="devices-device-dashboard-time-filter">
            {this.FILTER_BUTTONS.map((button, key) => (
              <Button key={key}
                      onClick={this.filterBy.bind(this, button.key)}
                      type={button.key === this.state.filter && 'primary' || 'default'}>
                {button.value}
              </Button>
            ))}
          </Button.Group>
          {/*<Button.Group className="dashboard-tools">*/}
          {/*{ this.state.editable && (*/}
          {/*<Button icon="check" onClick={this.finishEditDashboard.bind(this)}/>*/}
          {/*)}*/}
          {/*{ !this.state.editable && (*/}
          {/*<Button icon="tool" className="transparent" onClick={this.startEditDashboard.bind(this)}/>*/}
          {/*)}*/}

          {/*</Button.Group>*/}
        </div>

        <Widgets params={this.props.params} editable={this.state.editable} data={widgets} fetchRealData={true}/>

      </div>
    );
  }

}

export default Dashboard;
