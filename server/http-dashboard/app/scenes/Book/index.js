import React from 'react';
import {Layout, Menu} from 'antd';
import {Index, Fieldset, DeviceStatus, DeviceAuthToken, Section, Modal, ContentEditable, BackTop, Canvasjs} from './scenes/index';
import _ from 'lodash';
import './styles.less';
import 'highlightjs/styles/atom-one-light.css';

const {Content, Footer, Sider} = Layout;
const SubMenu = Menu.SubMenu;

class Book extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    children: React.PropTypes.any
  };

  state = {
    mode: 'inline',
  };

  docs = {
    Main: {
      Index: {
        path: "/book"
      }
    },
    'Data Display': {
      'Fieldset': {
        path: '/book/fieldset'
      },
      'Device Status': {
        path: '/book/device-status'
      },
      'DeviceAuthToken': {
        path: '/book/device-auth-token'
      },
      'Section': {
        path: '/book/section'
      },
      'Modal': {
        path: '/book/modal'
      },
      'ContentEditable': {
        path: '/book/content-editable'
      },
      'BackTop': {
        path: '/book/back-top'
      },
      'Canvasjs': {
        path: '/book/canvasjs'
      }
    }
  };

  handleMenuSelect(item) {
    this.context.router.push(item.key);
  }

  render() {

    return (
      <Layout className="blynk-book">
        <Sider>
          <div className="blynk-book-logo">Blynk Book</div>
          <Menu theme="dark" mode={this.state.mode} onSelect={this.handleMenuSelect.bind(this)}>
            { _.map(this.docs, (group, groupKey) => (
                <SubMenu key={groupKey} title={<span className="nav-text">{groupKey}</span>}>
                  { _.map(group, (item, itemKey) => (
                    <Menu.Item key={item.path}>{itemKey}</Menu.Item>
                  ))}
                </SubMenu>
              )
            )}
          </Menu>
        </Sider>
        <Layout>
          <Content style={{margin: '48px 16px 0'}}>
            <div style={{padding: 24, background: '#fff', minHeight: 360}}>
              { this.props.children }
            </div>
          </Content>
          <Footer style={{textAlign: 'center'}}>
            Blynk Book ©2017 Created by Ihor Brazhnichenko
          </Footer>
        </Layout>
      </Layout>
    );
  }

}

Book.Section = Section;
Book.ContentEditable = ContentEditable;
Book.DeviceAuthToken = DeviceAuthToken;
Book.DeviceStatus = DeviceStatus;
Book.Fieldset = Fieldset;
Book.Modal = Modal;
Book.BackTop = BackTop;
Book.Index = Index;
Book.Canvasjs = Canvasjs;

export default Book;
