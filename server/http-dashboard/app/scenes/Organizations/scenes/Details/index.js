import React                from 'react';
import {MainLayout}         from 'components';
import {connect}            from 'react-redux';
import {
  Button,
  Tabs
}                           from 'antd';
import PropTypes            from 'prop-types';
import {
  Map,
  List,
  fromJS
}                           from 'immutable';
import {bindActionCreators} from 'redux';
import {
  OrganizationsDetailsUpdate,
  OrganizationsFetch,
  OrganizationsUsersFetch
}                           from 'data/Organizations/actions';

import {
  OrganizationUsersDelete
}                           from 'data/Organization/actions';

import {
  Info,
  Products,
  Admins
}                           from './components';

const {TabPane} = Tabs;

import './styles.less';

@connect((state) => ({
  list: state.Organizations.get('list') || null,
  details: state.Organizations.get('details'),
}), (dispatch) => ({
  OrganizationsFetch: bindActionCreators(OrganizationsFetch, dispatch),
  OrganizationsUsersFetch: bindActionCreators(OrganizationsUsersFetch, dispatch),
  OrganizationUsersDelete: bindActionCreators(OrganizationUsersDelete, dispatch),
  OrganizationsDetailsUpdate: bindActionCreators(OrganizationsDetailsUpdate, dispatch),
}))
class Details extends React.Component {

  static contextTypes = {
    router: PropTypes.object
  };

  static propTypes = {
    list: PropTypes.instanceOf(List),
    details: PropTypes.instanceOf(Map),

    params: PropTypes.object,

    OrganizationsFetch: PropTypes.func,
    OrganizationsUsersFetch: PropTypes.func,
    OrganizationUsersDelete: PropTypes.func,
    OrganizationsDetailsUpdate: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleTabChange = this.handleTabChange.bind(this);
    this.handleUsersDelete = this.handleUsersDelete.bind(this);
  }

  componentWillMount() {

    const redirectIfNotExist = (list) => {
      if (!list.find(org => org.get('id') === Number(this.props.params.id)))
        this.context.router.push('/organizations/?notFound=true');
    };

    if (!this.props.list) {
      this.props.OrganizationsFetch().then((response) => {
        redirectIfNotExist(fromJS(response.payload.data));
      });
    }

    if (!this.props.details.get('users')) {
      this.props.OrganizationsUsersFetch({
        id: this.props.params.id
      });
    }

    if (this.props.list)
      redirectIfNotExist(this.props.list);

  }

  componentWillUnmount() {
    this.props.OrganizationsDetailsUpdate(
      this.props.details
        .set('activeTab', this.TABS.INFO)
        .set('users', null)
    );
  }

  TABS = {
    INFO: 'info',
    PRODUCTS: 'products',
    ADMINS: 'admins'
  };

  handleUsersDelete(ids) {
    this.props.OrganizationsDetailsUpdate(this.props.details.set('userDeleteLoading', true));
    return new Promise((resolve) => {
      this.props.OrganizationUsersDelete(this.props.params.id, ids).then(() => {
        this.props.OrganizationsUsersFetch({
          id: this.props.params.id
        }).then(() => {
          this.props.OrganizationsDetailsUpdate(this.props.details.set('userDeleteLoading', false));
          resolve(true);
        });
      });
    });
  }

  handleTabChange(tab) {
    this.props.OrganizationsDetailsUpdate(
      this.props.details.set('activeTab', tab)
    );
  }

  render() {

    if (!this.props.list)
      return null;

    if (!this.props.details.get('users'))
      return null;

    const organization = this.props.list.find(org => org.get('id') === Number(this.props.params.id));

    return (
      <MainLayout>
        <MainLayout.Header title={'Organization Name Is There'}
                           options={(
                             <div>
                               <Button type="danger" onClick={() => {
                               }}>Delete</Button>
                               <Button type="default" onClick={() => {
                               }}>Clone</Button>
                               <Button type="primary" onClick={() => {
                               }}>Edit</Button>
                             </div>
                           )}/>
        <MainLayout.Content className="product-details-content">
          <Tabs onChange={this.handleTabChange}
                activeKey={this.props.details.get('activeTab')}>
            <TabPane tab="Info"
                     key={this.TABS.INFO}>
              <div className="organizations-manage-tab-wrapper">
                <Info description={organization.get('description')}
                      logoUrl={organization.get('logoUrl')}
                      canCreateOrgs={organization.get('canCreateOrgs')}/>
              </div>
            </TabPane>
            <TabPane tab="Products"
                     key={this.TABS.PRODUCTS}>
              <div className="organizations-manage-tab-wrapper">
                <Products products={organization.get('products')}/>
              </div>
            </TabPane>
            <TabPane tab="Admins"
                     key={this.TABS.ADMINS}>
              <div className="organizations-manage-tab-wrapper">
                <Admins onUsersDelete={this.handleUsersDelete}
                        loading={this.props.details.get('userDeleteLoading')}
                        users={this.props.details.get('users').filter(user => user.get('role') === 'ADMIN')}/>
              </div>
            </TabPane>
          </Tabs>
        </MainLayout.Content>
      </MainLayout>
    );
  }

}

export default Details;
