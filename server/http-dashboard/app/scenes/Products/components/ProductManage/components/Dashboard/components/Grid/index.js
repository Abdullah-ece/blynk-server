import React from 'react';
import {
  Widgets
} from 'components';
import PropTypes from 'prop-types';
import {
  List
} from 'immutable';
import './styles.less';

class Grid extends React.Component {

  static propTypes = {
    widgets: PropTypes.instanceOf(List),

    params: PropTypes.shape({
      id: PropTypes.number.isRequired
    }).isRequired,

    onChange: PropTypes.func
  };

  constructor(props) {
    super(props);

    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(widgets) {
    this.props.onChange(widgets);
  }

  render() {

    if(!this.props.widgets)
      return null;

    const widgets = {
      lg: this.props.widgets.map(
        (item) => item.set('i', (item.get('id')).toString())
      ).toJS()
    };

    return (
      <div className="product-manage-dashboard-grid">
        <Widgets editable={true} data={widgets} onChange={this.handleChange} params={this.props.params}/>
      </div>
    );
  }

}

export default Grid;
