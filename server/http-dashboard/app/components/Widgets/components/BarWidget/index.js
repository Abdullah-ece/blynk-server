import React from 'react';
import {Icon} from 'antd';
import {
  Plotly
} from 'components';
import PropTypes from 'prop-types';
import './styles.less';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {Map} from 'immutable';
import {
  WidgetProductsFetch,
  WidgetOrganizationFetch,
  WidgetOrganizationsFetch,
} from 'data/Widgets/api';


/*

  THIS IS TESTING STYLED BAR WIDGET. IT WON'T BE USED ON PRODUCTION.

 */

@connect((state) => ({
  orgId: state.Account.orgId,
  widgets: state.Widgets && state.Widgets.get('widgetsData'),
}), (dispatch) => ({
  fetchProductsForWidget: bindActionCreators(WidgetProductsFetch, dispatch),
  fetchOrganization: bindActionCreators(WidgetOrganizationFetch, dispatch),
  fetchOrganizationsList: bindActionCreators(WidgetOrganizationsFetch, dispatch),
}))
class BarWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,
    params: PropTypes.object,

    editable: PropTypes.bool,

    fetchRealData: PropTypes.bool,

    orgId: PropTypes.number,
    typeOfData: PropTypes.number,

    fetchProductsForWidget: PropTypes.func,
    fetchOrganizationsList: PropTypes.func,
    fetchOrganization: PropTypes.func,
    onWidgetDelete: PropTypes.func,

    widgets: PropTypes.instanceOf(Map),
  };

  constructor(props) {
    super(props);

    this.handleHover = this.handleHover.bind(this);
    this.handleUnhover = this.handleUnhover.bind(this);
    this.handleMouseMove = this.handleMouseMove.bind(this);
    this.handleChartMouseEnter = this.handleChartMouseEnter.bind(this);
    this.handleChartMouseLeave = this.handleChartMouseLeave.bind(this);
  }

  state = {
    loading: true,
    data: []
  };

  componentWillMount() {
    this.setState({
      loading: true
    });

    if (this.props.data.typeOfData === 2) {
      this.props.fetchProductsForWidget().then(() => {
        this.setState({
          loading: false
        });
      });
    }

    if (this.props.data.typeOfData === 1 || this.props.data.typeOfData === 3) {
      const params = {
        orgId: this.props.orgId,
        deviceId: this.props.data.id
      };

      Promise.all([
        this.props.fetchOrganization(params),
        this.props.fetchOrganizationsList(params),
      ]).then(() => {
        this.setState({
          loading: false
        });
      });
    }

  }

  layout = {
    hovermode: 'y',
    hoverlabel: {
      bgcolor: 'transparent',
      bordercolor: 'transparent',
      font: {
        color: 'transparent'
      },
      bgcolorsrc: 'transparent',
    },
    hoveron: 'tonext',
    margin: {
      t: 25,
      b: 10,
      r: 15,
      l: 65,
    },
    yaxis: {
      fixedrange: true,
      showline: false,
      linecolor: 'rgb(204,204,204)',
      ticklen: 8,
      tickcolor: '#fff',
      tickfont: {
        family: 'PF DinDisplay Pro',
        size: '12',
        color: '#212227',
      },
      zeroline: false,
      hoverlabel: {
        bgcolor: 'transparent',
        bordercolor: 'transparent',
        font: {
          color: 'transparent'
        },
        bgcolorsrc: 'transparent',
      },
    },
    xaxis: {
      fixedrange: true,
      side: 'top',
      zeroline: true,
      showline: false,
      zerolinecolor: '#e2e2e2',
      linecolor: 'rgb(204,204,204)',
      tickfont: {
        family: 'PF DinDisplay Pro',
        size: '12',
        color: '#9a9a9a'
      },
      ticklen: 8,
      tickangle: 0,
      nticks: 12,
      tickcolor: '#fff',
    },
  };

  dataChartConfig = {};

  config = {
    displayModeBar: false
  };

  legendConfig = {
    width: [0.27, 0.27, 0.27],
    type: 'bar',
    orientation: 'h',
    marker: {color: 'rgba(33,179,130, 1)'},
    hoveron: 'points',
    hoverinfo: 'y',
  };

  handleChartMouseEnter() {
    this.hoverElement.style.display = 'block';
  }

  handleChartMouseLeave() {
    this.hoverElement.style.display = 'none';
  }

  handleHover(data, container, plotly) {

    const hoverColor = 'rgba(33,179,130, .28)';
    const staticColor = 'rgba(33,179,130, 1)';

    const color = new Array(data.points[0].data.x.length).fill(true).map((color, i) => i === data.points[0].pointNumber ? staticColor : hoverColor);

    const update = {
      marker: {
        color: color
      }
    };

    plotly.restyle(container, update, 0);

    this.hoverElement.innerHTML = `${data.points[0].x}`;

    this.hoverElement.style.opacity = 1;

    this.repositionHoverElement(data.event);

  }

  repositionHoverElement(event) {
    const boundingClient = this.chartElement.getBoundingClientRect();
    const hoverBoundingClient = this.hoverElement.getBoundingClientRect();

    let x = event.clientX - boundingClient.left;
    let y = event.clientY - boundingClient.top;

    this.hoverElement.style.top = `${y + 10 + (hoverBoundingClient.height / 2)}px`;
    this.hoverElement.style.left = `${x + 40}px`;
  }

  handleUnhover(data, container, plotly) {

    const staticColor = 'rgba(33,179,130, 1)';

    const update = {
      marker: {
        color: staticColor
      }
    };

    plotly.restyle(container, update, 0);

    this.hoverElement.style.opacity = 0;
  }

  handleMouseMove(event) {
    this.repositionHoverElement(event);
  }

  renderChartByParams(data = [], config = {}, layout = {}) {

    let maxYLabelWidth = 0;

    data.map((legend) => {
      // calculate max Y label width
      if (legend && Array.isArray(legend.y) && legend.y.length) {
        legend.y.forEach((label = '') => {
          if (String(label).length)
            maxYLabelWidth = label.length > maxYLabelWidth ? label.length : maxYLabelWidth;
        });
      }

      //calculate width of bars

      const countOfYLabels = legend && Array.isArray(legend.y) && legend.y.length || 1;

      const CELL_HEIGHT = 101;
      const TOP_PADDING = 50;
      const BOTTOM_PADDING = 30;
      const CHART_HEIGHT = this.props.data.h * CELL_HEIGHT - TOP_PADDING - BOTTOM_PADDING;
      const MAX_RATIO_BAR_WIDTH_TO_CHART = 0.9;

      const MIN_BAR_WIDTH_PX = 8;
      const MAX_BAR_WIDTH_PX = 40;

      const MIN_BAR_OFFSET_PX = 8;
      const MAX_BAR_OFFSET_PX = 16;

      const maxHeightForBar = CHART_HEIGHT / countOfYLabels;

      if (maxHeightForBar < MIN_BAR_WIDTH_PX + MIN_BAR_OFFSET_PX) {
        // if height is smaller then min height of bar
        legend.width = (new Array(countOfYLabels)).fill(MAX_RATIO_BAR_WIDTH_TO_CHART);
      } else if (maxHeightForBar >= (MIN_BAR_WIDTH_PX + MIN_BAR_OFFSET_PX) && maxHeightForBar <= (MAX_BAR_WIDTH_PX + MAX_BAR_OFFSET_PX)) {
        // if height is smaller than max and bigger then min

        let barHeight = maxHeightForBar / (MAX_BAR_WIDTH_PX + MAX_BAR_OFFSET_PX) * MAX_BAR_WIDTH_PX;

        let barWidthPercent = barHeight / maxHeightForBar;

        legend.width = (new Array(countOfYLabels)).fill(barWidthPercent);


      } else if (maxHeightForBar > (MAX_BAR_WIDTH_PX + MAX_BAR_OFFSET_PX)) {
        // if height is bigger than max

        let barHeightPercent = MAX_BAR_WIDTH_PX / maxHeightForBar;

        legend.width = (new Array(countOfYLabels)).fill(barHeightPercent);
      }

      return legend;
    });

    if (layout && layout.margin && layout.margin.l) {

      const AVG_SYMBOL_LENGTH = 6;

      layout.margin.l = Math.round(maxYLabelWidth * AVG_SYMBOL_LENGTH);
    }

    return (
      <div onMouseEnter={this.handleChartMouseEnter}
           onMouseLeave={this.handleChartMouseLeave}
           className="grid-linear-widget"
           ref={(element) => this.chartElement = element}
           onMouseMove={this.handleMouseMove}>
        <Plotly data={data} config={config} layout={layout} handleHover={this.handleHover}
                handleUnHover={this.handleUnhover}/>

        <div className="grid-bar-widget-chart-hover-container"
             ref={(element) => (this.hoverElement = element)}/>

      </div>
    );
  }

  renderDevicesByOrganization() {

    const data = [
      {
        x: [],
        y: [],
        ...this.legendConfig,
      }
    ];

    const devicesByOrganizationData = [];

    const addOrgToChart = (organization) => {

      const productsCount = organization && organization.get('products') && organization.get('products').size || 0;

      if(productsCount) {

        let devicesCount = 0;

        organization.get('products').forEach((product) => {
          devicesCount += product.get('deviceCount') || 0;
        });

        devicesByOrganizationData.push({
          x: devicesCount,
          y: organization.get('name'),
        });
      } else {
        devicesByOrganizationData.push({
          x: 0,
          y: organization.get('name'),
        });
      }
    };

    const orgs = this.props.widgets.getIn([String(this.props.data.id), 'organizations']);
    const currentOrg = this.props.widgets.getIn([String(this.props.data.id), 'organization']);

    if (orgs && orgs.forEach)
      orgs.forEach(addOrgToChart);

    if (currentOrg && currentOrg.get('name'))
      addOrgToChart(currentOrg);

    devicesByOrganizationData.sort((a, b) => a.x > b.x ? 1 : -1).forEach((item) => {
      data[0].x.push(item.x);
      data[0].y.push(item.y);
    });

    const config = {
      ...this.config,
    };

    const layout = {
      ...this.layout,
    };

    return this.renderChartByParams(data, config, layout);

  }

  renderDevicesByProduct() {
    const data = [
      {
        x: [],
        y: [],
        ...this.legendConfig,
      }
    ];

    const devicesByOrganizationData = [];

    this.props.widgets.getIn([String(this.props.data.id), 'data']).forEach((item) => {
      devicesByOrganizationData.push({
        x: item.get('deviceCount'),
        y: item.get('name'),
      });
    });

    devicesByOrganizationData.sort((a, b) => a.x > b.x ? 1 : -1).forEach((item) => {
      data[0].x.push(item.x);
      data[0].y.push(item.y);
    });

    const config = {
      ...this.config,
    };

    const layout = {
      ...this.layout,
    };

    return this.renderChartByParams(data, config, layout);
  }

  renderProductByOrganization() {
    const data = [
      {
        x: [],
        y: [],
        ...this.legendConfig,
      }
    ];

    const devicesByOrganizationData = [];

    const addOrgToChart = (organization, i) => {

      const productsCount = organization && organization.get('products') && organization.get('products').size || 0;

      devicesByOrganizationData.push({
        x: productsCount,
        // y: organization.get('name'),
        y: i === 0 ? 'Saphire' : organization.get('name'),
      });
    };

    const orgs = this.props.widgets.getIn([String(this.props.data.id), 'organizations']);
    const currentOrg = this.props.widgets.getIn([String(this.props.data.id), 'organization']);

    if (orgs && orgs.forEach)
      orgs.forEach(addOrgToChart);

    if (currentOrg && currentOrg.get('name'))
      addOrgToChart(currentOrg);

    devicesByOrganizationData.sort((a, b) => a.x > b.x ? 1 : -1).forEach((item) => {
      data[0].x.push(item.x);
      data[0].y.push(item.y);
    });

    const config = {
      ...this.config,
    };

    const layout = {
      ...this.layout,
    };

    return this.renderChartByParams(data, config, layout);
  }

  renderChartPreload() {
    return (
      <div className="grid-linear-widget">
        <div className="widget--chart-loading"><Icon type="loading"/></div>
      </div>
    );
  }

  render() {

    if (this.state.loading)
      return this.renderChartPreload();

    if (parseInt(this.props.data.typeOfData) === 1)
      return this.renderDevicesByOrganization();

    if (parseInt(this.props.data.typeOfData) === 2)
      return this.renderDevicesByProduct();

    // otherwise
    return this.renderProductByOrganization();
  }
}

export default BarWidget;
