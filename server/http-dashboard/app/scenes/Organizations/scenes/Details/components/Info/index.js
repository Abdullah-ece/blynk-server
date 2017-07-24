import React      from 'react';
import {
  Row,
  Col,
  Switch
}                 from 'antd';
import FormItem   from 'components/FormItem';
import PropTypes  from 'prop-types';

import './styles.less';

class Info extends React.Component {

  static propTypes = {
    logoUrl: PropTypes.string,
    description: PropTypes.string,

    canCreateOrgs: PropTypes.bool,
  };

  render() {
    return (
      <Row gutter={24}>
        <Col span={15}>
          { !!this.props.description && (
            <div className="organizations-details-row">
              <FormItem>
                <FormItem.Title>Description</FormItem.Title>
                <FormItem.Content>
                  {this.props.description}
                </FormItem.Content>
              </FormItem>
            </div>
          )}
          <div className="organizations-details-row">
            <FormItem>
              <FormItem.Content>
                <Switch size="small" checked={this.props.canCreateOrgs}/> <span
                className="switch-label">Can create orgs</span>
              </FormItem.Content>
            </FormItem>
          </div>
        </Col>
        <Col span={9}>
          { !!this.props.logoUrl && (
            <div className="organizations-details-row organizations-details-image">
              <img
                src={this.props.logoUrl}
                alt="Organization Logo"/>
            </div>
          )}
        </Col>
      </Row>
    );
  }

}

export default Info;
