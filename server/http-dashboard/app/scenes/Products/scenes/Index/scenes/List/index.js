import React        from 'react';
import {Link}       from 'react-router';
import {Button}     from 'antd';
import {connect}    from 'react-redux';
import ReactDom     from 'react-dom';
import {
  MainList,
  MainLayout
}                   from 'components';

import './styles.less';

@connect((state) => ({
  canCreateProducts: state.Organization && state.Organization.parentId === -1
}))
export default class ProductsList extends React.Component {

  static propTypes = {
    products: React.PropTypes.array,
    canCreateProducts: React.PropTypes.bool,
  };

  constructor(props) {
    super(props);

    this.handleWindowResize = this.handleWindowResize.bind(this);
  }

  componentDidMount() {
    this.handleWindowResize();

    window.addEventListener('resize', this.handleWindowResize);
  }

  componentWillUnmount() {
    window.removeEventListener("resize", this.handleWindowResize);
  }

  setMainLayoutContentRef = (node) => {
    this.mainLayoutContentNode = node;
  };

  setMainLayoutRef = (node) => {
    this.mainLayoutNode = node;
  };
  handleWindowResize() {
    const mainLayoutWidth = ReactDom.findDOMNode(this.mainLayoutNode).offsetWidth;
    const newWidth = (Math.floor(mainLayoutWidth / 200)) * 200; // Divided by width of one product card
    this.mainLayoutContentNode.style["max-width"] = newWidth + "px";
  }

  render() {
    return (
      <MainLayout setRef={this.setMainLayoutRef}>
        <MainLayout.Header title="Products" options={this.props.canCreateProducts && (
          <div>
            <Link to="/products/create">
              <Button icon="plus" type="primary">Create New Product</Button>
            </Link>
          </div>
        ) || null}/>
        <MainLayout.Content setRef={this.setMainLayoutContentRef}>
          <MainList>
            { this.props.products.map((product, key) => (
              <MainList.Item key={key}
                             logoUrl={product.logoUrl}
                             noImageText="No Product Image"
                             name={product.name}
                             devicesCount={product.deviceCount}
                             link={`/product/${product.id}`}/>
            ))}
          </MainList>
        </MainLayout.Content>
      </MainLayout>
    );
  }
}
