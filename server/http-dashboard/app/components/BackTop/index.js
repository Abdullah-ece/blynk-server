import React                                from 'react';
import {BackTop as AntBackTop, Icon, Badge} from 'antd';
import _                                    from 'lodash';
import './styles.less';

class BackTop extends React.Component {

  static propTypes = {
    badgeCount: React.PropTypes.number
  };

  shouldComponentUpdate(nextProps) {

    if (!_.isEqual(this.props, nextProps))
      return true;

    return false;
  }

  render() {

    const props = _.omit(this.props, [
      'badgeCount'
    ]);

    return (
      <AntBackTop {...props}>
        <div className="ant-back-top-inner">
          <Badge count={this.props.badgeCount}/>
          <Icon type="arrow-up"/>
        </div>
      </AntBackTop>
    );
  }

}

export default BackTop;
