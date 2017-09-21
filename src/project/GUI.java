package project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * GUI class creates a user graphic interface,and it is responsible for all the
 * iterations with user including accepting the correct input and make correct change 
 * in data when user perform operation. 
 * 
 */
public class GUI {
	private int session = -1;
	private JFrame f_main;
	private Project project = new Project();
	private User current_user = null;
	private JPanel proPanel = new JPanel();
	private JFrame cart_frame = null;
	
	
	/**
	 * Construct a GUI that is has a size 600*600
	 */
	
	private GUI(){
		f_main = new JFrame("Welcome");
		f_main.setSize(600, 600);
		f_main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar mb = menuBar();
		f_main.setJMenuBar(mb);
		initProPanel();
		f_main.setVisible(true);
	}

	private JMenuBar menuBar(){
		JMenuBar mb = new JMenuBar();
		JMenu user_menu = user();  // the 0 index in the menu bar
		mb.add(user_menu);
		JMenu shopper_menu = shopperMenu(); // the 1 index of the menu bar
		shopper_menu.setVisible(false);
		mb.add(shopper_menu);

		// Browse Options Menu
		mb.add(browseOptions());
		return mb;
	}

// ================================ menu of user =========================
	// ================================ menu of user =========================
		private  JMenu user(){
			JMenu m = new JMenu("User");

			JMenuItem log_in = new JMenuItem("Log in");
			JMenuItem register = new JMenuItem("Register");
			JMenuItem log_out = new JMenuItem("Log out");
			log_out.setEnabled(false);

			m.add(log_in);
			m.add(register);
			m.add(log_out);

			log_in.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					loginFrame();
				}});

			log_out.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					logoutFrame();
				}});

			register.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					registerFrame();
				}});

			return m;
		}

		// ---------------------- the log_out frame ----------------------
		private void logoutFrame(){
			/*
				if(session == -1){
					JOptionPane.showMessageDialog(null, "NO USER LOGGED IN");
					return;
				}
			 */
			project.logout(session);
			session = -1;
			current_user = null;
			f_main.getJMenuBar().getMenu(1).setVisible(false); // the shopper bar
			f_main.getJMenuBar().getMenu(2).setVisible(false); // the browse bar
			showAllPros();

			JMenuBar mb = menuBar();
			//set menu enabled
			mb.getMenu(0).getMenuComponent(0).setEnabled(true);
			mb.getMenu(0).getMenuComponent(2).setEnabled(false);
			f_main.setJMenuBar(mb);
			f_main.setVisible(true);

			JOptionPane.showMessageDialog(null, "Logout successful");
		}

		//  --------------------------------- the register frame  ---------------------------------
		private void registerFrame(){

			JFrame register_frame = new JFrame("Register");
			register_frame.setSize(400, 300);
			JPanel p = new JPanel();
			p.setLayout(new GridLayout(0,2));

			// UserID and Password
			JTextField userID = new JTextField(15);
			JTextField password = new JTextField(15);		

			addLabel("User Name: ", p);
			addBox(userID,p);
			addLabel("PassWord: ", p);
			addBox(password,p);

			// UserType Chooser
			final JRadioButton admin = new JRadioButton("Administrator", true);
			final JRadioButton shopper = new JRadioButton("Shopper");
			ButtonGroup group = new ButtonGroup();
			group.add(admin);
			group.add(shopper);
			p.add(admin);
			p.add(shopper);

			JButton register = new JButton("Register");
			register.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean userType;
					if (admin.isSelected())
						userType = true;
					else
						userType = false;
					if(isBlankSpace(userID.getText())||isBlankSpace(password.getText())) JOptionPane.showMessageDialog(null, "Invalid Input");
					else{
					boolean check = project.addUser(userID.getText(), password.getText(),userType);
					if(check)
						JOptionPane.showMessageDialog(null, "SUCCESSFULL");
					else
						JOptionPane.showMessageDialog(null, "failed to register");
					register_frame.dispose();
				}};
			});
			JPanel new_p = new JPanel();
			new_p.add(register);
			p.add(new_p);

			register_frame.add(p);
			register_frame.setVisible(true);
		}

		//  --------------------- the log in frame  ----------------------
		private void loginFrame(){
			/*
			// first step to prevent one interface logged in two user at the same time
			if(session >= 0){
				JOptionPane.showMessageDialog(null, "Already Logged in");
				return;
			}
			 */
			JFrame log_in_frame = new JFrame("USER LOG IN");

			log_in_frame.setSize(300, 300);

			JPanel p = new JPanel();
			p.setLayout(new GridLayout(6,2));
			JTextField[] tf = new JTextField[5];
			for (int i = 0; i < tf.length; i++)
				tf[i] = new JTextField(20);

			addLabel("User Name",p);
			addBox(tf[0],p);
			addLabel("PassWord",p);
			addBox(tf[1],p);

			JButton sure = new JButton("confirm");
			sure.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					int check = project.login(tf[0].getText(), tf[1].getText());
					session = check;
					if(check == -1){
						JOptionPane.showMessageDialog(null, "LOG IN \n FAILED!");

					}
					else if(check > 0){
						JOptionPane.showMessageDialog(null, "LOG IN successful");
						current_user = project.fm.getUser(tf[0].getText()); 
						if(current_user instanceof Administrator){
							JMenuBar mb = menuBar();
							//set menu enabled
							mb.getMenu(0).getMenuComponent(0).setEnabled(false);
							mb.getMenu(0).getMenuComponent(2).setEnabled(true);

							JMenu admin = admin();
							mb.add(admin);
							showAllProsAdmin();
							f_main.setJMenuBar(mb);
							f_main.setVisible(true);
						}
						if(current_user instanceof Shopper){
							JMenuBar mb = menuBar();
							//set menu enabled
							mb.getMenu(0).getMenuComponent(0).setEnabled(false);
							mb.getMenu(0).getMenuComponent(2).setEnabled(true);

							mb.getMenu(1).setVisible(true);
							f_main.setJMenuBar(mb);
							f_main.setVisible(true);
							
							// start to check the cart
							ArrayList<Product> warning = new ArrayList<Product>();
							for(Product pr:((Shopper)current_user).Cart.keySet()){
								Integer previous_cart_amount = ((Shopper)current_user).Cart.get(pr);
								if(previous_cart_amount <= 0)
									warning.add(pr);
							}
							// start to deal with the warning cart_item
							if(warning.size() != 0){
								String warning_str = "Failed to add back to cart: \n";
								for(int i=0;i<warning.size();i++){
									Product current = warning.get(i);
									// one unit error, should be some method delete product in the cart in shopper
									((Shopper)current_user).Cart.remove(current);
									project.fm.writeUsers();
									warning_str += current.description +"\n";
								}
								JOptionPane.showMessageDialog(null, warning_str);
							}
							showAllPros();
							
						}
						f_main.getJMenuBar().getMenu(2).setVisible(true);
					}
					log_in_frame.setVisible(false);
				}
			});
			JPanel new_p = new JPanel();
			new_p.add(sure);
			p.add(new_p);

			log_in_frame.add(p);
			log_in_frame.setVisible(true);
		}
		
	// ========================== Admin ==========================
	private JMenu admin(){
		JMenu m = new JMenu("Administrator");

		JMenu sort = new JMenu("Sort");

		JMenuItem add_cate = new JMenuItem("Add Category");
		JMenuItem report_cate = new JMenuItem("Report Category");
		JMenuItem add_prod = new JMenuItem("Add Product");
		JMenuItem sort_category = new JMenuItem("Sort by Category");
		JMenuItem sort_ava_incre = new JMenuItem("Sort by Availability in increasing order");
		JMenuItem sort_ava_decre = new JMenuItem("Sort by Availability in decreasing order");
		JMenuItem add_FR = new JMenuItem("Add Flight Route");
		JMenuItem add_DC = new JMenuItem("Add Distribution Center");
		JMenuItem show_to_admin = new JMenuItem("Show all items to admin");
		JMenuItem sales_report = new JMenuItem("Sales Report");
		
		show_to_admin.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				showAllProsAdmin();				
			}
		});
		m.add(show_to_admin);
		m.add(add_cate);
		m.add(report_cate);
		m.add(add_prod);
		sort.add(sort_category);
		sort.add(sort_ava_incre);
		sort.add(sort_ava_decre);
		m.add(add_FR);
		m.add(add_DC);
		m.add(sort);
		m.add(sales_report);

		report_cate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				reportCateFrame();
			}});

		
		add_cate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addCateFrame();
			}});

		add_prod.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addProdFrame();
			}});

		add_FR.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addFRFrame();
			}});

		add_DC.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addDCFrame();
			}});

		sort_ava_incre.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				sortProPanel(((Administrator)current_user).sortIncreasing());
			}});
		
		sort_ava_decre.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				sortProPanel(((Administrator)current_user).sortDecreasing());
			}});

		sort_category.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				sortByCate(((Administrator)current_user).sortbyCategories());
			}});
		
		sales_report.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				salesReports();
			}});
		
		return m;
	}

	//====Add Category, YUE==================
	private void addCateFrame(){
		JFrame Add_Cate_Frame = new JFrame();

		Add_Cate_Frame.setSize(500,500);

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(6,2));
		JTextField[] tf = new JTextField[5];
		for (int i = 0; i < tf.length; i++)
			tf[i] = new JTextField(20);

		addLabel("Category Name",p);
		addBox(tf[0],p);

		JButton sure = new JButton("Confirm");
		sure.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(isBlankSpace(tf[0].getText())) JOptionPane.showMessageDialog(null,"Invalid Input");
				else{
				int check = project.addCategory(tf[0].getText(),session);
				if(check==-1){
					JOptionPane.showMessageDialog(null,"Fail to Add Category !");
				}
				else if(check>0){
					JOptionPane.showMessageDialog(null,"Add Category Successful !");
				}
				showAllProsAdmin();
				Add_Cate_Frame.setVisible(false);
			}}
		});

		JPanel p1 = new JPanel();
		p1.add(sure);
		p.add(p1);

		Add_Cate_Frame.add(p);
		Add_Cate_Frame.setVisible(true);
	}

	private void addProdFrame(){
		JFrame Add_Prod_Frame = new JFrame();

		Add_Prod_Frame.setSize(500,500);

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0, 2));
		JTextField[] tf = new JTextField[5];
		for (int i = 0; i < tf.length; i++)
			tf[i] = new JTextField(20);

		addLabel("Product Name",p);
		addBox(tf[0],p);
		
		addLabel("Category", p);
		// Combo box used to choose category
		JPanel CateListPanel = new JPanel(new WrapLayout());
		int cateCounter = project.fm.categories.size();
		String[] cates = new String[cateCounter];
		for (int i=0;i<project.fm.categories.size();i++) {
			cates[i] = project.fm.categories.get(i).description;
		}
		@SuppressWarnings({ "rawtypes", "unchecked" })
		JComboBox cateList = new JComboBox(cates);
		cateList.setPreferredSize(new Dimension(200, 25));
		CateListPanel.add(cateList);
		p.add(CateListPanel);
		
		addLabel("Price",p);
		addBox(tf[2],p);

		JButton sure = new JButton("Confirm");
		sure.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Category cat = project.fm.getCategory((String)cateList.getSelectedItem());
				boolean isDouble = isDouble(tf[2].getText());
				
				if(cat == null) JOptionPane.showMessageDialog(null,"Wrong format for product's Category !");
				else if(!isDouble||isBlankSpace(tf[0].getText())) JOptionPane.showMessageDialog(null,"Invalid Input");
				else{
					int check = project.addProduct(tf[0].getText(), cat.code, Double.parseDouble(tf[2].getText()),session);
					if(check==-1){
						JOptionPane.showMessageDialog(null,"Fail to Add Product !");
					}
					else if(check>0){
						JOptionPane.showMessageDialog(null,"Add New Product Successful !");
					}
					showAllProsAdmin();
					Add_Prod_Frame.setVisible(false);
				}

			}

		});

		JPanel p1 = new JPanel();
		p1.add(sure);
		p.add(p1);
		
		Add_Prod_Frame.add(p);
		Add_Prod_Frame.setVisible(true);
		
	}

	private void addFRFrame(){
		JFrame Add_FR_Frame = new JFrame();

		Add_FR_Frame.setSize(500,500);

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(6,2));
		JTextField[] tf = new JTextField[5];
		for (int i = 0; i < tf.length; i++)
			tf[i] = new JTextField(20);

		addLabel("City A",p);
		addBox(tf[0],p);
		addLabel("City B",p);
		addBox(tf[1],p);
		addLabel("Distance",p);
		addBox(tf[2],p);

		JButton sure = new JButton("Confirm");
		sure.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				boolean isInt = isInteger(tf[2].getText());

				if(!isInt||isBlankSpace(tf[0].getText())) JOptionPane.showMessageDialog(null,"Invalid Input");

				else{
					project.addRoute(tf[0].getText(),tf[1].getText(),Integer.parseInt(tf[2].getText()),session);
					JOptionPane.showMessageDialog(null,"Add Route Successful !");
				}
				showAllProsAdmin();
				Add_FR_Frame.setVisible(false);
			}

		});

		JPanel p1 = new JPanel();
		p1.add(sure);
		p.add(p1);

		Add_FR_Frame.add(p);
		Add_FR_Frame.setVisible(true);

	}

	private void addDCFrame(){
		JFrame Add_DC_Frame = new JFrame();

		Add_DC_Frame.setSize(500,500);

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0,1));

		p.add(new Label("Choose City: "));
		
		JPanel c = new JPanel();
		c.setLayout(new WrapLayout());
		int cityCounter = project.fm.map.cities.size();
		String[] cities = new String[cityCounter];
		for (int i=0;i<cityCounter;i++) {
			cities[i] = project.fm.map.cities.get(i);
		}
		@SuppressWarnings({ "rawtypes", "unchecked" })
		JComboBox citiesList = new JComboBox(cities);
		citiesList.setPreferredSize(new Dimension(200, 25));
		c.add(citiesList);
		p.add(c);
		
		JButton sure = new JButton("Confirm");
		sure.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

				project.addDistributionCenter((String)citiesList.getSelectedItem(), session);
				JOptionPane.showMessageDialog(null,"Add Distribution Center Successful !");
				showAllProsAdmin();
				Add_DC_Frame.setVisible(false);
			}

		});

		JPanel p1 = new JPanel();
		p1.add(sure);
		p.add(p1);

		Add_DC_Frame.add(p);
		Add_DC_Frame.setVisible(true);
	}
	
	private void sortProPanel(List<Product> s) {
		proPanel.removeAll();
		proPanel.setLayout(new WrapLayout());
		for (Product p : s)
			if (p.totalQty > 0)
				proPanel.add(oneProductForAdmin(p));
		proPanel.revalidate();
		proPanel.repaint();
	}
	
	private void sortByCate(HashMap<Category, List<Product>> m) {
		proPanel.removeAll();
		proPanel.setLayout(new GridLayout(0,1));
		for (Category c : m.keySet()) {
			Label catLbl = new Label(c.description);
			catLbl.setFont(new Font("Courier New", Font.ITALIC, 50));
			proPanel.add(catLbl);
			for (Product p : m.get(c)) {
				proPanel.add(oneProductForAdmin(p));
			}
		}
		proPanel.revalidate();
		proPanel.repaint();
	}
	
	private void showAllProsAdmin() {
		proPanel.setLayout(new WrapLayout());
		proPanel.removeAll();
		for (Product p : project.fm.products)
			proPanel.add(oneProductForAdmin(p));
		proPanel.revalidate();
		proPanel.repaint();
	}

	private JPanel oneProductForAdmin(Product p) {
		// A panel for one product
		JPanel onePro = new JPanel();
		onePro.setLayout(new BorderLayout());
		onePro.setMaximumSize(new Dimension(500, 150));
		
		// Information of this Product
		JPanel proInfo = new JPanel();
		proInfo.setLayout(new BoxLayout(proInfo, BoxLayout.Y_AXIS));
		Label empty1 = new Label(" ");
		Label proName = new Label(p.description);
		Label proID = new Label("Product ID: " + p.ID);
		Label proCategory = new Label("Category: " + project.fm.getCategory(p.cateCode).description);
		Label proPrice = new Label("Price: " + new Double(p.price).toString());
		Label proQty = new Label("Available Quantity: " + new Integer(p.totalQty).toString());
		Label empty2 = new Label(" ");
		proInfo.add(empty1);
		proInfo.add(proName);
		proInfo.add(proID);
		proInfo.add(proCategory);
		proInfo.add(proPrice);
		proInfo.add(proQty);
		proInfo.add(empty2);

	// Maintain Section
		JPanel mtnSect = new JPanel();
		mtnSect.setLayout(new GridLayout(0, 1));
		mtnSect.setBorder(new EmptyBorder(100, 20, 100, 20));
		
		// Update quantity section
		JButton updQtyButton = new JButton("Update Quantiy");
		updQtyButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (project.fm.map.DCs.size() > 0)
					updQtyBtn(p);
				else
					JOptionPane.showMessageDialog(null, "No Distribution Center Exists yet!");
			}});
		mtnSect.add(updQtyButton);

		// Set Description Button
		JButton setDescButton = new JButton("Set Description");
		setDescButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setDescDialog(p);
			}});
		mtnSect.add(setDescButton);
		
		// Set Image Button
		JButton setImgButton = new JButton("Set Image");
		setImgButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setImgDialog(p);
			}});
		mtnSect.add(setImgButton);
		
		// Set Price Button
		JButton setPrcButton = new JButton("Set Price");
		setPrcButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setPrcDialog(p);
			}});
		mtnSect.add(setPrcButton);
		
	// Image of the product
		JPanel proImage = new JPanel();
		proImage.setLayout(new BorderLayout());
		proImage.setPreferredSize(new Dimension(200, 200));
		if (p.image != null) {
			JLabel label = new JLabel(new ImageIcon(p.image));
			label.setMaximumSize(new Dimension(150, 150));
			proImage.add(label);
		}

		// Add all components to the Frame
		onePro.add(proImage, BorderLayout.WEST);
		onePro.add(proInfo, BorderLayout.CENTER);
		onePro.add(mtnSect, BorderLayout.EAST);
		return onePro;
	}
	
	private JFrame updQtyBtn(Product p) {
		JFrame updframe = new JFrame("Enter Quantity");
		updframe.setSize(new Dimension(200, 200));
		updframe.getContentPane().setLayout(new GridLayout(0, 2));

		JTextField amountField = new JTextField();

		int dcCounter = project.fm.map.DCs.size();
		String[] dcs = new String[dcCounter];
		for (int i=0;i<project.fm.map.DCs.size();i++) {
			dcs[i] = project.fm.map.DCs.get(i);
		}
		@SuppressWarnings({ "rawtypes", "unchecked" })
		JComboBox dcList = new JComboBox(dcs);

		JButton enter = new JButton("Update");
		enter.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!isInteger(amountField.getText())) JOptionPane.showMessageDialog(null,"Invalid Input");
				else{
					project.fm.readProducts();
					project.updateQuantity(p.ID, (String)dcList.getSelectedItem(), 
							Integer.parseInt(amountField.getText()), session);
					showAllProsAdmin();
					updframe.dispose();
					project.fm.writeProducts();
				}
			}
		});
		updframe.add(new Label("Enter Quantity: "));
		updframe.add(amountField);
		updframe.add(new Label("Enter Distribution Center: "));
		updframe.add(dcList);
		updframe.add(enter);
		updframe.setVisible(true);
		return updframe;
	}

	private void setDescDialog(Product p) {
		JFrame setFrame = new JFrame("Set Description");
		setFrame.setSize(new Dimension(200, 200));
		setFrame.getContentPane().setLayout(new GridLayout(0, 1));

		JTextField txtField = new JTextField();

		JButton enter = new JButton("Set");
		enter.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isBlankSpace(txtField.getText())) JOptionPane.showMessageDialog(null,"Invalid Input");
				else{
				project.fm.readProducts();
				if(project.fm.getProduct(txtField.getText()) != null) {
					JOptionPane.showMessageDialog(null, "this description already exist!");
					return;
				}
				p.setDescription(txtField.getText());
				project.fm.writeProducts();
				showAllProsAdmin();
				setFrame.dispose();}
			}});
		setFrame.add(new Label("Set Description: "));
		setFrame.add(txtField);
		setFrame.add(enter);
		setFrame.setVisible(true);
	}
	
	private void setPrcDialog(Product p) {
		JFrame setFrame = new JFrame("Set Price");
		setFrame.setSize(new Dimension(200, 200));
		setFrame.getContentPane().setLayout(new GridLayout(0, 1));

		JTextField txtField = new JTextField();

		JButton enter = new JButton("Set");
		enter.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!isDouble(txtField.getText())) JOptionPane.showMessageDialog(null,"Invalid Input");
				else{
				project.fm.readProducts();
				p.setPrice(Double.parseDouble(txtField.getText()));
				project.fm.writeProducts();
				showAllProsAdmin();
				setFrame.dispose();}
			}});
		
		setFrame.add(new Label("Set Price: "));
		setFrame.add(txtField);
		setFrame.add(enter);
		setFrame.setVisible(true);
	}
	
	private void setImgDialog(Product p) {
		JFrame setFrame = new JFrame("Set Image");
		setFrame.setSize(new Dimension(200, 200));
		setFrame.getContentPane().setLayout(new GridLayout(0, 1));

		JTextField txtField = new JTextField();
		
		JButton enter = new JButton("Set");
		enter.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isBlankSpace(txtField.getText())) JOptionPane.showMessageDialog(null,"Invalid Input");
				else{
				project.fm.readProducts();
				p.setImage(txtField.getText());
				project.fm.writeProducts();
				showAllProsAdmin();
				setFrame.dispose();}
			}});
		
		setFrame.add(new Label("Set Image: "));
		setFrame.add(txtField);
		setFrame.add(enter);
		setFrame.setVisible(true);
	}
	
	private void reportCateFrame(){
		JFrame Report_frame = new JFrame();
		
		Report_frame.setSize(800, 800);
		
		JPanel p = new JPanel();

		String[] report = ((Administrator)current_user).report().split(" ");
		p.setLayout(new GridLayout(report.length,1));
		
		for(int i=0;i<report.length;i++){
			addLabel(report[i],p);
		}
		
		
		JButton sure = new JButton("OK");
		
		sure.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e) {
				Report_frame.setVisible(false);
			}
		});
		
		JPanel new_p = new JPanel();
		new_p.add(sure);
		p.add(new_p);
		
		
		Report_frame.add(p);
		Report_frame.setVisible(true);
		
		
	}
	
	private void salesReports() {
		JFrame sales = new JFrame();
		sales.setSize(450, 600);
		sales.setLayout(new GridLayout(0, 1));
		JPanel allReport = new JPanel();
		
		int dim = 3;
		for (String city : project.fm.map.cities) {
			boolean w = true;
			for (Invoice in : project.fm.invoices) {
				if (city.equals(in.shopperCity)) {
					dim += 1;
					if (w) {
						JPanel aCity = new JPanel();
						aCity.setPreferredSize(new Dimension(400, 100));
						Label c = new Label(city);
						c.setFont(new Font("Courier New", Font.ITALIC, 50));
						aCity.add(c);
						allReport.add(aCity);
						w = false;
						dim += 1;
					}
					allReport.add(oneReport(in));
				}
			}
		}
		allReport.setPreferredSize(new Dimension(400, dim*100));
		JScrollPane proPane = new JScrollPane(allReport);
		proPane.getVerticalScrollBar().setUnitIncrement(120);
		sales.add(proPane);
		sales.setVisible(true);
	}
	
	private JPanel oneReport(Invoice inv) {
		JPanel report = new JPanel();
		report.setPreferredSize(new Dimension(400, 100));
		report.setLayout(new GridLayout(0,4));
		String[] s = inv.date.split(" ");
		report.add(new Label(s[1] +" "+ s[2]));
		report.add(new Label(s[3]));
		report.add(new Label(s[5]));
		report.add(new Label(" "));

		report.add(new Label("Product ID"));
		report.add(new Label("Product Name"));
		report.add(new Label("Quantity"));
		report.add(new Label("Sales Amount"));
		
		report.add(new Label("============"));
		report.add(new Label("============"));
		report.add(new Label("============"));
		report.add(new Label("============"));
		
		double total = 0;
		
		for (int i : inv.items.keySet()) {
			Product p = project.fm.getProduct(i);
			report.add(new Label(new Integer(p.ID).toString()));
			report.add(new Label(p.description));
			report.add(new Label(new Integer(inv.items.get(i)).toString()));
			double amount = inv.items.get(i) * p.price;
			total += amount;
			report.add(new Label(new Double(amount).toString()));
		}
		
		report.add(new Label("TOTAL: "));
		report.add(new Label(" "));
		report.add(new Label(" "));
		report.add(new Label(new Double(total).toString()));
		return report;
	}

	// ====================== The Shopper function button on the menu====================
	private JMenu shopperMenu(){

		JMenu m = new JMenu("Shopper");

		JMenuItem profile = new JMenuItem("My Account");
		m.add(profile);
		JMenuItem view_cart = new JMenuItem("My Cart");
		m.add(view_cart);
		JMenuItem invs = new JMenuItem("All Invoices");
		m.add(invs);

		profile.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				profileFrame();
			}});

		view_cart.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				viewCartFrame();
			}});
		
		invs.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				viewInvoiceFrame();
			}});

		return m;

	}

	// ------------------------------- shopper profile_frame -------------------------
	private void profileFrame(){
		JFrame profile_frame = new JFrame("My Account");
		profile_frame.setSize(300, 600);
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0,1));
		if(current_user == null) return;
		if(current_user instanceof Administrator) return;
		addLabel("", p);
		addLabel("          My UserName:  "+current_user.userID, p);
		addLabel("          Type:  SHOPPER",p);
		if(((Shopper)current_user).cust == null){
			addLabel("          Not a Registed Customer Yet!",p);
		}
		if(((Shopper)current_user).cust != null){
			String[] s = ((Shopper)current_user).cust.toString().split(",");
			addLabel("          Custmer ID:   "+s[0],p);
			addLabel("          Custmer Name:   "+s[1],p);
			addLabel("          Custmer City:   "+s[2],p);
			addLabel("          Custmer Street:   "+s[3],p);
		}
		
		
		JButton close = new JButton("close");
		close.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				profile_frame.setVisible(false);
			}});

		JPanel new_p = new JPanel();
		new_p.add(close);
		p.add(new_p);

		profile_frame.add(p);
		profile_frame.setVisible(true);
	}

	// ------------------------------- shopper view_cart_frame -------------------------
    private void viewCartFrame(){
		
		if(current_user == null) return;
		if(current_user instanceof Shopper){
			cart_frame = new JFrame("My Cart");
			cart_frame.setSize(800, 800);
			
			Container cart_cont = new Container();
			cart_cont.setLayout(new GridLayout(0, 1));
			JScrollPane cart_panel = new JScrollPane();
			cart_panel.removeAll();
			cart_panel = new JScrollPane(cart_cont);
			cart_panel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
			
			addBottom("product","quantity","price"," "," ",cart_cont);
			
			double total_price = 0;
			for(Product pr:((Shopper) current_user).Cart.keySet()){
				double single = pr.price;
				total_price += single * ((Shopper) current_user).Cart.get(pr);
				JPanel n_p = addMiddleProduct(pr);
				cart_cont.add(n_p);
			}
			if(total_price == 0) 
				addTop(" "," "," "," "," ",cart_cont);
			
			//p.add(proPane);
			addBottom("Products total Price","",""+total_price," "," ",cart_cont);
			if(((Shopper)current_user).cust == null){
				addBottom("Shipping amount","","0"," "," ",cart_cont);
			}
			
			if(((Shopper)current_user).cust != null){
				if(total_price == 0){
					addBottom("Shipping amount","","0"," "," ",cart_cont);
					addBottom("total amount","","0"," "," ",cart_cont);
				}
				
				else{
					String cust_city = (((Shopper)current_user).cust.city);
					ArrayList<String> path = project.fm.map.ShortestRoute(cust_city);
					int distance = project.fm.map.PathDistance(path);
					double shipping_price = distance * 0.01;
					addBottom("Shipping amount","",""+shipping_price," "," ",cart_cont);
					addBottom("total amount","",total_price + shipping_price + ""," "," ",cart_cont);
				}
			}
			// should be a total price here
			
			JPanel p1 = new JPanel(new GridLayout(1,0));

			JButton check_out = new JButton("check out");
			check_out.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					if(((Shopper) current_user).cust == null) return;
					if(((Shopper)current_user).Cart.size() == 0){
						JOptionPane.showMessageDialog(null, "Empty cart");
						return;
					}
					int check = ((Shopper)current_user).checkout();
					if(check == -1) JOptionPane.showMessageDialog(null, "check out failed!");
					else{
					cart_frame.setVisible(false);
					viewCartFrame();
					showAllPros();
					JOptionPane.showMessageDialog(null, "Check out successful");
					}
				}
			});
			
			JPanel p_1 = new JPanel(new FlowLayout());
			p_1.setBackground(Color.black);
			p_1.add(check_out);
			p1.add(p_1);

			JLabel label_middle = new JLabel("");
			label_middle.setForeground(Color.white);
			JPanel p_2 = new JPanel(new FlowLayout());
			p_2.setBackground(Color.black);
			p_2.add(label_middle);
			p1.add(p_2);

			JButton cancel_cart = new JButton("cancel this cart");
			cancel_cart.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(((Shopper) current_user).cust == null) return;
					((Shopper)current_user).cancelCart();
					cart_frame.setVisible(false);
					viewCartFrame();
					showAllPros();
					JOptionPane.showMessageDialog(null, "Empty cart successful");
				}
				
			});
			JPanel p_3 = new JPanel(new FlowLayout());
			p_3.setBackground(Color.black);
			p_3.add(cancel_cart);
			p1.add(p_3);

			p1.setBackground(Color.black);

			cart_cont.add(p1);
			
			cart_frame.add(cart_panel);
			cart_frame.setVisible(true);
		}
	}
	
	private void addTop(String left,String middle,String right,String righter,String rightest,Container cont){
		JPanel p1 = new JPanel(new GridLayout(1,0));
		p1.setSize(new Dimension(400,150));
		JLabel label_left = new JLabel(left);
		label_left.setForeground(Color.black);
		JPanel p_1 = new JPanel(new FlowLayout());
		p_1.setBackground(Color.white);
		p_1.add(label_left);
		p1.add(p_1);

		JLabel label_middle = new JLabel(middle);
		label_middle.setForeground(Color.black);
		JPanel p_2 = new JPanel(new FlowLayout());
		p_2.setBackground(Color.white);
		p_2.add(label_middle);
		p1.add(p_2);

		JLabel label_right = new JLabel(right);
		label_right.setForeground(Color.black);
		JPanel p_3 = new JPanel(new FlowLayout());
		p_3.setBackground(Color.white);
		p_3.add(label_right);
		p1.add(p_3);

		JLabel label_righter = new JLabel(righter);
		label_righter.setForeground(Color.black);
		JPanel p_4 = new JPanel(new FlowLayout());
		p_4.setBackground(Color.white);
		p_4.add(label_righter);
		p1.add(p_4);
		
		JLabel label_rightest = new JLabel(rightest);
		label_rightest.setForeground(Color.black);
		JPanel p_5 = new JPanel(new FlowLayout());
		p_5.setBackground(Color.white);
		p_5.add(label_rightest);
		p1.add(p_5);
		
		p1.setBackground(Color.white);

		cont.add(p1);
	}

	private JPanel addMiddleProduct(Product prod){
		String left = prod.description;
		String middle = ""+((Shopper) current_user).Cart.get(prod);
		double single = prod.price;
		String right = "" + single * ((Shopper) current_user).Cart.get(prod);
		
		JPanel p1 = new JPanel(new GridLayout(1,0));
		p1.setSize(new Dimension(400,150));

		JLabel label_left = new JLabel(left);
		label_left.setForeground(Color.black);
		JPanel p_1 = new JPanel(new FlowLayout());
		p_1.setBackground(Color.white);
		p_1.add(label_left);
		p1.add(p_1);

		JLabel label_middle = new JLabel(middle);
		label_middle.setForeground(Color.black);
		JPanel p_2 = new JPanel(new FlowLayout());
		p_2.setBackground(Color.white);
		p_2.add(label_middle);
		p1.add(p_2);

		JLabel label_right = new JLabel(right);
		label_right.setForeground(Color.black);
		JPanel p_3 = new JPanel(new FlowLayout());
		p_3.setBackground(Color.white);
		p_3.add(label_right);
		p1.add(p_3);
		
		JButton change = new JButton("change quantity");
		change.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame amount_frame = new JFrame("Enter Quantity");
				amount_frame.setSize(new Dimension(200, 200));
				amount_frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));

				JTextField amountField = new JTextField();
				amountField.setPreferredSize(new Dimension(100, 20));

				JButton enter = new JButton("Enter");
				enter.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (isBlankSpace(amountField.getText())||!isInteger(amountField.getText())) JOptionPane.showMessageDialog(null, "Invalid Input");
						else{
							int totalqty = (((Shopper)current_user).Cart.get(prod));
							totalqty += prod.totalQty;
							int set_to_quantity = Integer.parseInt(amountField.getText());
							if (totalqty >= set_to_quantity && set_to_quantity >= 0) {
								((Shopper)current_user).changeQuantity(prod, Integer.parseInt(amountField.getText()));
								amount_frame.dispose();
								cart_frame.setVisible(false);
								viewCartFrame();
								showAllPros();
								}
							else{
								JOptionPane.showMessageDialog(null, "Invalid Input");
							}
						}
					}
				});
				amount_frame.add(new Label("Enter Amount: "));
				amount_frame.add(amountField);
				amount_frame.add(enter);
				amount_frame.setVisible(true);
				
				
			}
			
		});
		JPanel p_4 = new JPanel(new FlowLayout());
		p_4.add(change);
		p1.add(p_4);
		
		JButton remove = new JButton("remove");
		remove.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				cart_frame.setVisible(false);
				((Shopper)current_user).removeProd(prod);
				viewCartFrame();
				showAllPros();
				JOptionPane.showMessageDialog(null, "remove item successful");
			}
			
		});
		JPanel p_5 = new JPanel(new FlowLayout());
		p_5.add(remove);
		p1.add(p_5);
		
		p1.setBackground(Color.white);
		return p1;
	}
	
	
	private void addBottom(String left,String middle,String right,String righter,String rightest,Container cont){
		JPanel p1 = new JPanel(new GridLayout(1,0));

		JLabel label_left = new JLabel(left);
		label_left.setForeground(Color.white);
		JPanel p_1 = new JPanel(new FlowLayout());
		p_1.setBackground(Color.black);
		p_1.add(label_left);
		p1.add(p_1);

		JLabel label_middle = new JLabel(middle);
		label_middle.setForeground(Color.white);
		JPanel p_2 = new JPanel(new FlowLayout());
		p_2.setBackground(Color.black);
		p_2.add(label_middle);
		p1.add(p_2);

		JLabel label_right = new JLabel(right);
		label_right.setForeground(Color.white);
		JPanel p_3 = new JPanel(new FlowLayout());
		p_3.setBackground(Color.black);
		p_3.add(label_right);
		p1.add(p_3);
		
		JLabel label_righter = new JLabel(righter);
		label_righter.setForeground(Color.white);
		JPanel p_4 = new JPanel(new FlowLayout());
		p_4.setBackground(Color.black);
		p_4.add(label_righter);
		p1.add(p_4);
		
		JLabel label_rightest = new JLabel(rightest);
		label_rightest.setForeground(Color.white);
		JPanel p_5 = new JPanel(new FlowLayout());
		p_5.setBackground(Color.black);
		p_5.add(label_rightest);
		p1.add(p_5);
		
		p1.setBackground(Color.black);

		cont.add(p1);
	}
	
	// ------------------------------- shopper view_invoice history frame -------------------------
	private void viewInvoiceFrame(){
		Shopper sh = (Shopper)current_user;
		List<Invoice> invs = sh.purchases();
		JFrame sales = new JFrame();
		sales.setSize(450, 600);
		sales.setLayout(new GridLayout(0, 1));
		JPanel allReport = new JPanel();
		int dim = 2;
		for (Invoice in : invs){
			allReport.add(oneInvoice(in));
			dim += 1;
		}
		allReport.setPreferredSize(new Dimension(400, dim*100));
		JScrollPane proPane = new JScrollPane(allReport);
		proPane.getVerticalScrollBar().setUnitIncrement(200);
		
		sales.add(proPane);
		sales.setVisible(true);

	}
	
	private JPanel oneInvoice(Invoice inv){
		JPanel report = new JPanel();
		report.setPreferredSize(new Dimension(400, 100));
		report.setLayout(new GridLayout(0,4));
		String[] s = inv.date.split(" ");
		report.add(new Label(s[1] +" "+ s[2]));
		report.add(new Label(s[3]));
		report.add(new Label(s[5]));
		report.add(new Label(" "));

		report.add(new Label("Product ID"));
		report.add(new Label("Product Name"));
		report.add(new Label("Quantity"));
		report.add(new Label("Sales Amount"));
		
		report.add(new Label("============"));
		report.add(new Label("============"));
		report.add(new Label("============"));
		report.add(new Label("============"));
		
		double total = 0;
		
		for (int i : inv.items.keySet()) {
			Product p = project.fm.getProduct(i);
			report.add(new Label(new Integer(p.ID).toString()));
			report.add(new Label(p.description));
			report.add(new Label(new Integer(inv.items.get(i)).toString()));
			double amount = inv.items.get(i) * p.price;
			total += amount;
			report.add(new Label(new Double(amount).toString()));
		}
		
		report.add(new Label("Delivery Charge: "));
		report.add(new Label(" "));
		report.add(new Label(" "));
		report.add(new Label(new Double(inv.delCharge).toString()));

		total += inv.delCharge;
		
		report.add(new Label("TOTAL: "));
		report.add(new Label(" "));
		report.add(new Label(" "));
		report.add(new Label(new Double(total).toString()));
		return report;
	}
	// ================================ BROWSE ================================
	// =================== browse ===================
	private JMenu browseOptions() {
		JMenu browseOp = new JMenu("Browse");
		browseOp.add(browse());
		JMenuItem allPros = new JMenuItem("Show All Products");
		allPros.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showAllPros();
			}
		});
		browseOp.add(allPros);
		return browseOp;
	}

	private JMenuItem browse() {
		JMenuItem mi = new JMenuItem("Browse");
		ActionListener a = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame browse_frame = new JFrame("Browse");
				browse_frame.setSize(300, 300);
				JPanel p = new JPanel();
				p.setLayout(new GridLayout(0,2));
				Label l1 = new Label("Category: ");
				Label l2 = new Label("Minimum Price: ");
				Label l3 = new Label("Maximum Price: ");
				Label empty = new Label(" ");

				// Combo box used to choose category
				int cateCounter = project.fm.categories.size();
				String[] cates = new String[cateCounter];
				for (int i=0;i<project.fm.categories.size();i++) {
					cates[i] = project.fm.categories.get(i).description;
				}
				@SuppressWarnings({ "rawtypes", "unchecked" })
				JComboBox cateList = new JComboBox(cates);
				TextField min = new TextField();
				TextField max = new TextField();
				JButton browse = new JButton("Browse");
				browse.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						String selectedCate = (String) cateList.getSelectedItem();
						if(!isDouble(min.getText())|| !isDouble(max.getText())) JOptionPane.showMessageDialog(null,"Invalid Input");
						else browsePro(selectedCate, min.getText(), max.getText());
					}
				});

				p.add(l1);
				p.add(cateList);
				p.add(l2);
				p.add(min);
				p.add(l3);
				p.add(max);
				p.add(empty);
				p.add(browse);
				browse_frame.add(p);
				browse_frame.setVisible(true);
			}
		};
		mi.addActionListener(a);
		return mi;
	}

	private void browsePro(String CategoryName, String minPrice, String maxPrice) {
		proPanel.removeAll();
		proPanel.setLayout(new WrapLayout());
		double min = Double.parseDouble(minPrice);
		double max = Double.parseDouble(maxPrice);
		for (Product p : User.browse(CategoryName, min, max)) {
			if (current_user != null && current_user instanceof Administrator)
				proPanel.add(oneProductForAdmin(p));
			else
				proPanel.add(oneProduct(p));
		}
		proPanel.validate(); 
		proPanel.repaint();
	}

	// =================== all products ===================
	private void initProPanel() {
		showAllPros();
		JScrollPane proPane = new JScrollPane(proPanel);
		proPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
		f_main.add(proPane);
		proPane.getVerticalScrollBar().setUnitIncrement(80);
		//proPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
	}

	private void showAllPros() {
		proPanel.setLayout(new WrapLayout());
		proPanel.removeAll();
		for (Product p : project.fm.products)
			if (p.totalQty > 0)
				proPanel.add(oneProduct(p));
		proPanel.revalidate();
		proPanel.repaint();
	}
	
	private JPanel oneProduct(Product p) {
		// A panel for one product
		JPanel onePro = new JPanel();
		onePro.setLayout(new BorderLayout());
		onePro.setMaximumSize(new Dimension(500, 150));
		
		// Information of this Product
		JPanel proInfo = new JPanel();
		proInfo.setLayout(new BoxLayout(proInfo, BoxLayout.Y_AXIS));
		Label empty1 = new Label(" ");
		Label proName = new Label(p.description);
		Label proID = new Label("Product ID: " + p.ID);
		Label proCategory = new Label("Category: " + project.fm.getCategory(p.cateCode).description);
		Label proPrice = new Label("Price: " + new Double(p.price).toString());
		Label proQty = new Label("Available Quantity: " + new Integer(p.totalQty).toString());
		Label empty2 = new Label(" ");
		proInfo.add(empty1);
		proInfo.add(proName);
		proInfo.add(proID);
		proInfo.add(proCategory);
		proInfo.add(proPrice);
		proInfo.add(proQty);
		proInfo.add(empty2);

		// add to cart button
		JPanel cartButton = new JPanel();
		cartButton.setLayout(new BorderLayout());
		cartButton.setBorder(new EmptyBorder(100, 20, 100, 20));
		JButton addToCart = new JButton();
		addToCart.setText("Add to Cart");
		addToCart.setSize(new Dimension(100, 25));
		addToCart.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (current_user == null)
					loginFrame();
				else if(current_user instanceof Administrator){
					return;
				}
				
				else if(current_user instanceof User && ((Shopper)current_user).cust == null){
					// if not a customer, add it as a customer
						JFrame cust_frame = new JFrame("customer register");
						cust_frame.setSize(400,400);
						cust_frame.getContentPane().setLayout(new GridLayout(0,1));
						
						cust_frame.setPreferredSize(new Dimension(100,20));
						
						cust_frame.add(new Label("              This is your first time shopping here"));
						cust_frame.add(new Label("                 you need a customer registration"));
						
						cust_frame.add(new Label("Enter Customer Name"));
						JTextField cust_name = new JTextField();
						cust_frame.add(cust_name);
						
						cust_frame.add(new Label("Choose Customer City"));
						int cityCounter = project.fm.map.cities.size();
						String[] cities = new String[cityCounter];
						for (int i=0;i<cityCounter;i++) {
							cities[i] = project.fm.map.cities.get(i);
						}
						@SuppressWarnings({ "rawtypes", "unchecked" })
						JComboBox citiesList = new JComboBox(cities);
						citiesList.setPreferredSize(new Dimension(200, 25));
						cust_frame.add(citiesList);
						
						cust_frame.add(new Label("Enter Customer Street"));
						JTextField cust_street = new JTextField();
						cust_frame.add(cust_street);
						
						JPanel p1 = new JPanel(new GridLayout(1,0));
						JPanel p_1 = new JPanel();
						p_1.add(new Label(""));
						JPanel p_2 = new JPanel();
						JButton enter = new JButton("enter");
						enter.addActionListener(new ActionListener(){

							@Override
							public void actionPerformed(ActionEvent e) {
								
								if(isBlankSpace(cust_name.getText())||isBlankSpace(cust_street.getText())) JOptionPane.showMessageDialog(null, "Invalid Input");
								else{
								int check = project.addCustomer(cust_name.getText(), (String)citiesList.getSelectedItem(), cust_street.getText(), session);
								if(check == -1){
									JOptionPane.showMessageDialog(null, "Customer Register Failed!");
								}
								else{
									JOptionPane.showMessageDialog(null, "Customer Register Successful");
									cust_frame.setVisible(false);
								}}
							}});
						
						p_2.add(enter);
						p1.add(p_1);
						p1.add(p_2);
						cust_frame.add(p1);
						cust_frame.setVisible(true);
					
				}
				else {
					JFrame amount_frame = new JFrame("Enter Quantity");
					amount_frame.setSize(new Dimension(200, 200));
					amount_frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));

					JTextField amountField = new JTextField();
					amountField.setPreferredSize(new Dimension(100, 20));

					JButton enter = new JButton("Enter");
					enter.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent arg0) {
							if (isBlankSpace(amountField.getText())||!isInteger(amountField.getText())) JOptionPane.showMessageDialog(null, "Invalid Input");
							else{	
							((Shopper)current_user).addtoCart(p.ID, Integer.parseInt(amountField.getText()));
								amount_frame.dispose();
								showAllPros();
							}}
						}
					);
					amount_frame.add(new Label("Enter Amount: "));
					amount_frame.add(amountField);
					amount_frame.add(enter);
					amount_frame.setVisible(true);
				}
			}});
		cartButton.add(addToCart, BorderLayout.CENTER);

		// Image of the product
		JPanel proImage = new JPanel();
		proImage.setLayout(new BorderLayout());
		proImage.setPreferredSize(new Dimension(200, 200));
		//image = image.getScaledInstance(150, 150,  java.awt.Image.SCALE_SMOOTH);
		if (p.image != null) {
			JLabel label = new JLabel(new ImageIcon(p.image));
			label.setMaximumSize(new Dimension(150, 150));
			proImage.add(label);
		}

		onePro.add(proImage, BorderLayout.WEST);
		onePro.add(proInfo, BorderLayout.CENTER);
		onePro.add(cartButton, BorderLayout.EAST);
		return onePro;
	}

	// ================================ helper function ====================================
	private void addLabel(String labeltext,JPanel panel){
		JLabel label = new JLabel(labeltext);
		JPanel a_p = new JPanel(new GridLayout(0,1));
		a_p.add(label);
		panel.add(a_p);
	}

	private void addBox(JTextField tField, JPanel panel) {
		JPanel a_p = new JPanel();
		a_p.add(tField);
		panel.add(a_p);
	}

	private static boolean isInteger(String s) {
		try { 
			Integer.parseInt(s); 
		} catch(NumberFormatException e) { 
			return false; 
		} catch(NullPointerException e) {
			return false;
		}
		return Integer.parseInt(s)>=0;
	}

	private static boolean isDouble(String s) {
		try { 
			Double.parseDouble(s); 
		} catch(NumberFormatException e) { 
			return false; 
		} catch(NullPointerException e) {
			return false;
		}
		return Double.parseDouble(s)>=0;
	}
	
	private boolean isBlankSpace(String s){
		if(s.trim().length()==0 || s.contains(","))
			return true;
		else return false;
	}
	

	public static void main(String[] args) {
		new GUI();
	}

}